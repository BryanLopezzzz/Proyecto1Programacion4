import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { registrarOferente } from '../../api/api'

const validarEmail = (correo) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(correo.trim())
const validarTelefono = (tel) => /^\+506 \d{4} \d{4}$/.test(tel.trim())

export default function RegistroOferente() {
  const nav = useNavigate()
  const [f, setF] = useState({ identificacion: '', nombre: '', primerApellido: '', nacionalidad: '', residencia: '', telefono: '', correo: '', clave: '', clave2: '' })
  const [error, setError] = useState('')
  const [ok, setOk] = useState('')

  const set = k => e => setF(prev => ({ ...prev, [k]: e.target.value }))

  const submit = async () => {
    setError(''); setOk('')
    if (!f.identificacion.trim()) return setError('La identificación es requerida')
    if (!f.nombre.trim()) return setError('El nombre es requerido')
    if (!f.primerApellido.trim()) return setError('El primer apellido es requerido')
    if (!f.nacionalidad.trim()) return setError('La nacionalidad es requerida')
    if (!f.residencia.trim()) return setError('La residencia es requerida')
    if (!validarTelefono(f.telefono)) return setError('El teléfono debe tener el formato +506 XXXX XXXX')
    if (!validarEmail(f.correo)) return setError('El correo no tiene un formato válido (ej: usuario@dominio.com)')
    if (f.clave.length < 6) return setError('Contraseña mínimo 6 caracteres')
    if (f.clave !== f.clave2) return setError('Las claves no coinciden')
    try {
      const res = await registrarOferente(f)
      setOk(res.mensaje + ' Redirigiendo al login…')
      setTimeout(() => nav('/login'), 2000)
    } catch (e) { setError(e.message) }
  }

  return (
      <div className="auth-centrado" style={{ padding: '40px 20px' }}>
        <div className="auth-box">
          <h2 style={{ textAlign: 'center', marginBottom: 22 }}>Registro de Oferente</h2>
          {error && <div className="alert-error">{error}</div>}
          {ok && <div className="alert-success">{ok}</div>}
          <label>Identificación *</label><input type="text" value={f.identificacion} onChange={set('identificacion')} placeholder="1-2345-6789" />
          <label>Nombre *</label><input type="text" value={f.nombre} onChange={set('nombre')} placeholder="Juan" />
          <label>Primer apellido *</label><input type="text" value={f.primerApellido} onChange={set('primerApellido')} placeholder="Pérez" />
          <label>Nacionalidad *</label><input type="text" value={f.nacionalidad} onChange={set('nacionalidad')} placeholder="Costarricense" />
          <label>Residencia *</label><input type="text" value={f.residencia} onChange={set('residencia')} placeholder="San José, Costa Rica" />
          <label>Teléfono * (formato +506 XXXX XXXX)</label><input type="text" value={f.telefono} onChange={set('telefono')} placeholder="+506 8888 8888" />
          <label>Correo *</label><input type="email" value={f.correo} onChange={set('correo')} placeholder="correo@ejemplo.com" autoComplete="email" />
          <label>Contraseña * (mín. 6)</label><input type="password" value={f.clave} onChange={set('clave')} placeholder="••••••" autoComplete="new-password" />
          <label>Confirmar contraseña *</label><input type="password" value={f.clave2} onChange={set('clave2')} placeholder="••••••" autoComplete="new-password" />
          <button className="btn btn-primary" style={{ width: '100%', marginTop: 22 }} onClick={submit}>Registrar Oferente</button>
          <div className="auth-links" style={{ marginTop: 12 }}>
            <a onClick={() => nav('/login')} style={{ cursor: 'pointer' }}>Ya tengo cuenta → Iniciar sesión</a>
          </div>
        </div>
      </div>
  )
}