import keycloak from "./keycloak"

const TOKEN_MIN_VALIDITY_SECONDS = 70

export async function updateToken () {
    await keycloak.updateToken(TOKEN_MIN_VALIDITY_SECONDS);
    return keycloak.token;
}