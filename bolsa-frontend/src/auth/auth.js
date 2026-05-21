export const Auth = {
  save(token, role, correo) {
    localStorage.setItem('be_token', token)
    localStorage.setItem('be_role', role)
    localStorage.setItem('be_user', correo)
  },
  getToken: () => localStorage.getItem('be_token'),
  getRole: () => localStorage.getItem('be_role'),
  getUser: () => localStorage.getItem('be_user'),
  isLogged: () => !!localStorage.getItem('be_token'),
  logout() {
    localStorage.removeItem('be_token')
    localStorage.removeItem('be_role')
    localStorage.removeItem('be_user')
  }
}
