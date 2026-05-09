/**
 * auth.js — Gestión de sesión JWT en localStorage.
 */

const Auth = {
    KEY_TOKEN: 'be_token',
    KEY_ROLE:  'be_role',
    KEY_USER:  'be_user',

    save(token, role, correo) {
        localStorage.setItem(this.KEY_TOKEN, token);
        localStorage.setItem(this.KEY_ROLE,  role);
        localStorage.setItem(this.KEY_USER,  correo);
    },

    getToken()  { return localStorage.getItem(this.KEY_TOKEN); },
    getRole()   { return localStorage.getItem(this.KEY_ROLE);  },
    getUser()   { return localStorage.getItem(this.KEY_USER);  },
    isLogged()  { return !!this.getToken(); },

    logout() {
        localStorage.removeItem(this.KEY_TOKEN);
        localStorage.removeItem(this.KEY_ROLE);
        localStorage.removeItem(this.KEY_USER);
        Router.go('/');
    },

    requireRole(role) {
        if (!this.isLogged() || this.getRole() !== role) {
            Router.go('/login');
            return false;
        }
        return true;
    }
};