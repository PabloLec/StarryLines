echo "GITHUB_TOKEN: ${GITHUB_TOKEN}"

./gradlew downloadApolloSchema \
  --endpoint="https://api.github.com/graphql" \
  --schema="src/main/graphql/schema/github/schema.json" \
  --header="Authorization: Bearer ${GITHUB_TOKEN}"