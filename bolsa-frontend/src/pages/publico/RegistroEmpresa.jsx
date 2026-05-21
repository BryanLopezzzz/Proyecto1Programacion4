import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { registrarEmpresa } from '../../api/api'

export default function RegistroEmpresa() {
  const nav = useNavigate()
  const [f, setF] = useState({ nombre: '', correo: '', clave: '', clave2: '', localizacion: '', telefono: '', descripcion: '' })
  const [error, setError] = useState('')
  const [ok, setOk] = useState('')

  const set = k => e => setF(prev => ({ ...prev, [k]: e.target.value }))

  const submit = async () => {
    setError(''); setOk('')
    if (f.clave.length < 6) return setError('Contraseña mínimo 6 caracteres')
    if (f.clave !== f.clave2) return setError('Las claves no coinciden')
    try {
      const res = await registrarEmpresa(f)
      setOk(res.mensaje + ' Redirigiendo al login…')
      setTimeout(() => nav('/login'), 2000)
    } catch (e) { setError(e.message) }
  }

  return (
    <div className="auth-centrado" style={{ padding: '40px 20px' }}>
      <div className="auth-box">
        <h2 style={{ textAlign: 'center', marginBottom: 22 }}>Registro de Empresa</h2>
        {error && <div className="alert-error">{error}</div>}
        {ok && <div className="alert-success">{ok}</div>}
        <label>Nombre *</label><input type="text" value={f.nombre} onChange={set('nombre')} placeholder="TechCorp S.A." />
        <label>Correo *</label><input type="email" value={f.correo} onChange={set('correo')} placeholder="contacto@empresa.com" />
        <label>Contraseña * (mín. 6)</label><input type="password" value={f.clave} onChange={set('clave')} placeholder="••••••" />
        <label>Confirmar contraseña *</label><input type="password" value={f.clave2} onChange={set('clave2')} placeholder="••••••" />
        <label>Localización *</label><input type="text" value={f.localizacion} onChange={set('localizacion')} placeholder="San José, Costa Rica" />
        <label>Teléfono * (formato +506 XXXX XXXX)</label><input type="text" value={f.telefono} onChange={set('telefono')} placeholder="+506 8888 8888" />
        <label>Descripción *</label><textarea value={f.descripcion} onChange={set('descripcion')} placeholder="Descripción de la empresa…" />
        <button className="btn btn-primary" style={{ width: '100%', marginTop: 22 }} onClick={submit}>Registrar Empresa</button>
        <div className="auth-links" style={{ marginTop: 12 }}>
          <a onClick={() => nav('/login')} style={{ cursor: 'pointer' }}>Ya tengo cuenta → Iniciar sesión</a>
        </div>
      </div>
    </div>
  )
}
