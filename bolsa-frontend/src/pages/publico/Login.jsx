import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login } from '../../api/api'
import { useAuth } from '../../context/AuthContext'

export default function Login() {
  const [user, setUser] = useState('')
  const [pass, setPass] = useState('')
  const [error, setError] = useState('')
  const { saveAuth, isLogged, role } = useAuth()
  const nav = useNavigate()

  if (isLogged) {
    if (role === 'ROLE_EMPRESA') nav('/empresa/dashboard')
    else if (role === 'ROLE_OFERENTE') nav('/oferente/dashboard')
    else if (role === 'ROLE_ADMIN') nav('/admin/dashboard')
  }

  const handleLogin = async () => {
    setError('')
    try {
      const data = await login(user, pass)
      saveAuth(data.token, data.role, data.correo)
      if (data.role === 'ROLE_EMPRESA') nav('/empresa/dashboard')
      else if (data.role === 'ROLE_OFERENTE') nav('/oferente/dashboard')
      else if (data.role === 'ROLE_ADMIN') nav('/admin/dashboard')
    } catch (e) { setError(e.message) }
  }

  const onKey = e => { if (e.key === 'Enter') handleLogin() }

  return (
    <div className="auth-centrado">
      <div className="auth-box narrow">
        <div className="logo">🏢 BolsaEmpleo</div>
        <div className="logo-sub">Inicia sesión en tu cuenta</div>
        {error && <div className="alert-error">{error}</div>}
        <label>Usuario</label>
        <input type="text" value={user} onChange={e => setUser(e.target.value)} onKeyDown={onKey} autoFocus placeholder="Correo o Identificación" />
        <label>Contraseña</label>
        <input type="password" value={pass} onChange={e => setPass(e.target.value)} onKeyDown={onKey} placeholder="••••••••" />
        <button className="btn btn-primary" style={{ width: '100%', marginTop: 24 }} onClick={handleLogin}>Entrar</button>
        <div className="auth-links" style={{ marginTop: 20 }}>
          <a onClick={() => nav('/registro/empresa')} style={{ cursor: 'pointer' }}>Registrar Empresa</a>
          <span className="divider">|</span>
          <a onClick={() => nav('/registro/oferente')} style={{ cursor: 'pointer' }}>Registrar Oferente</a>
          <br /><br />
          <a onClick={() => nav('/')} style={{ cursor: 'pointer' }}>← Volver al inicio</a>
        </div>
      </div>
    </div>
  )
}
