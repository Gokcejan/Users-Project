#!/bin/sh
set -e
CLONETO="build/api-docs"
DESTRELATIVE="src/main/resources/doc-api-adoc/app-users-management/"
DEST="$CLONETO/$DESTRELATIVE"

ADOC_DIR="./server/build/doc-api-adoc/"

cd `dirname "$0"`
if test '!' -d "$ADOC_DIR"
then
    echo "Build the docs first!" >&2
    exit 1
fi

git clone "https://csp2adocs:$CSP2ADOCPASSWORD@gitlab.acrobits.cz/csp2/api-docs.git" "$CLONETO"
rm -rf "$DEST"
mkdir "$DEST"
cp ./server/src/main/resources/doc-api-adoc/* "$DEST"
cp -r ./server/build/doc-api-adoc/* "$DEST"/

cd "$CLONETO"
if test -z "$(git status --porcelain)"
then
    echo "No changes, nothing to commit"
    exit 0
fi

git add "$DESTRELATIVE"
git config user.email csp2+adoc@acrobits.cz
git config user.name "Gitlab Runner"
git commit -m "Authentication Service from $(git rev-parse --short HEAD)"
git pull
git push

