import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('be_token'))
  const [role, setRole] = useState(() => localStorage.getItem('be_role'))
  const [user, setUser] = useState(() => localStorage.getItem('be_user'))

  function saveAuth(t, r, u) {
    localStorage.setItem('be_token', t)
    localStorage.setItem('be_role', r)
    localStorage.setItem('be_user', u)
    setToken(t); setRole(r); setUser(u)
  }

  function logout() {
    localStorage.clear()
    setToken(null); setRole(null); setUser(null)
  }

  return (
    <AuthContext.Provider value={{ token, role, user, saveAuth, logout, isLogged: !!token }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
