#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DOCKERFILE="$ROOT_DIR/docker/Dockerfile.maven"

usage() {
    cat <<EOF
Usage: $(basename "$0") <project-name> [options]

Builds a Docker image for the lab project in ./<project-name>.

Options:
  -t, --tag TAG         Image tag (default: <project-name>:latest)
      --java-version N   Java version for the base images (default: 21)
      --skip-tests       Skip tests during the Maven build
      --no-cache         Pass --no-cache to docker build
      --run              Run the built image after a successful build
  -p, --port PORT       Host port to publish when --run is used (default: 8080)
  -h, --help             Show this help message

Examples:
  $(basename "$0") soap
  $(basename "$0") soap --skip-tests --run
  $(basename "$0") soap --tag soap:dev -p 9090 --run
EOF
}

project_name=""
tag=""
java_version="21"
skip_tests="false"
no_cache=()
run_after_build="false"
host_port="8080"

while [[ $# -gt 0 ]]; do
    case "$1" in
        -t|--tag)
            tag="$2"
            shift 2
            ;;
        --java-version)
            java_version="$2"
            shift 2
            ;;
        --skip-tests)
            skip_tests="true"
            shift
            ;;
        --no-cache)
            no_cache=(--no-cache)
            shift
            ;;
        --run)
            run_after_build="true"
            shift
            ;;
        -p|--port)
            host_port="$2"
            shift 2
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        -*)
            echo "Unknown option: $1" >&2
            usage
            exit 1
            ;;
        *)
            if [[ -n "$project_name" ]]; then
                echo "Unexpected argument: $1" >&2
                usage
                exit 1
            fi
            project_name="$1"
            shift
            ;;
    esac
done

if [[ -z "$project_name" ]]; then
    echo "Error: project name is required." >&2
    usage
    exit 1
fi

project_dir="$ROOT_DIR/$project_name"

if [[ ! -d "$project_dir" ]]; then
    echo "Error: '$project_dir' does not exist." >&2
    exit 1
fi

if [[ ! -f "$project_dir/pom.xml" ]]; then
    echo "Error: '$project_name' does not look like a Maven project (pom.xml not found)." >&2
    exit 1
fi

if [[ ! -x "$project_dir/mvnw" ]]; then
    echo "Error: '$project_name/mvnw' not found or not executable." >&2
    exit 1
fi

tag="${tag:-$project_name:latest}"

echo "==> Building image '$tag' for project '$project_name' (Java $java_version)"

docker build \
    "${no_cache[@]}" \
    -f "$DOCKERFILE" \
    --build-arg "PROJECT_NAME=$project_name" \
    --build-arg "JAVA_VERSION=$java_version" \
    --build-arg "SKIP_TESTS=$skip_tests" \
    -t "$tag" \
    "$ROOT_DIR"

echo "==> Built image: $tag"

if [[ "$run_after_build" == "true" ]]; then
    echo "==> Running $tag on http://localhost:$host_port"
    docker run --rm -p "$host_port:8080" "$tag"
fi
