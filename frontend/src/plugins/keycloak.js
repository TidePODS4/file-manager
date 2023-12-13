import Keycloak from 'keycloak-js';
import { updateToken } from './keycloak-util';

const keycloak = new Keycloak({
    url: 'http://localhost:8080',
    realm: 'File-Manager',
    clientId: 'file-server-frontend'
});

try {
    const authenticated = await keycloak.init({
        onLoad: 'check-sso',
        checkLoginIframe: true,
        // redirectUri: window.location.origin,
        // postLogoutRedirectUri: window.location.origin,
    });

    if (authenticated){
        window.onfocus = () => {
            updateToken()
        }
    }
} catch (error) {
    console.error('Failed to initialize adapter:', error);
}

export default keycloak;