export function BarraCoincidencia({ pct }) {
  return (
    <div className="bar-wrap">
      <div className="bar-outer">
        <div className="bar-inner" style={{ width: `${pct}%` }} />
      </div>
      <span className="bar-label">{pct.toFixed(1)}%</span>
    </div>
  )
}

export function nivelTexto(n) {
  return n === 1 ? 'Básico' : n === 2 ? 'Intermedio' : 'Avanzado'
}

export function ArbolCheckboxes({ raices, seleccionadas = [], onChange, mostrarNivel = false, niveles = {}, onNivelChange }) {
  const renderNodo = (nodo, depth = 0) => {
    const checked = seleccionadas.includes(nodo.id)
    return (
      <li key={nodo.id} style={{ marginLeft: depth * 16, margin: `6px 0 6px ${depth * 16}px` }}>
        <label style={{ display: 'flex', alignItems: 'center', gap: 7, cursor: 'pointer', margin: 0, fontWeight: 400 }}>
          <input
            type="checkbox"
            checked={checked}
            onChange={() => {
              if (checked) onChange(seleccionadas.filter(id => id !== nodo.id))
              else onChange([...seleccionadas, nodo.id])
            }}
          />
          <span>{nodo.nombre}</span>
        </label>
        {mostrarNivel && (
          <select
            className="nivel-sel"
            value={niveles[nodo.id] || 2}
            onChange={e => onNivelChange && onNivelChange(nodo.id, parseInt(e.target.value))}
          >
            <option value={1}>Básico</option>
            <option value={2}>Intermedio</option>
            <option value={3}>Avanzado</option>
          </select>
        )}
        {nodo.hijos && nodo.hijos.length > 0 && (
          <ul style={{ listStyle: 'none', padding: 0 }}>
            {nodo.hijos.map(h => renderNodo(h, depth + 1))}
          </ul>
        )}
      </li>
    )
  }

  return (
    <ul style={{ listStyle: 'none', padding: 0 }}>
      {raices.map(r => renderNodo(r))}
    </ul>
  )
}

export function Toast({ msg, ok = true, onClose }) {
  return (
    <div
      className={ok ? 'alert-success' : 'alert-error'}
      style={{ position: 'fixed', top: 20, right: 20, zIndex: 9999, minWidth: 260, padding: '14px 18px', cursor: 'pointer' }}
      onClick={onClose}
    >
      {msg}
    </div>
  )
}

export function useToast() {
  const [toast, setToast] = React.useState(null)
  const showToast = (msg, ok = true) => {
    setToast({ msg, ok })
    setTimeout(() => setToast(null), 3500)
  }
  const ToastEl = toast ? <Toast msg={toast.msg} ok={toast.ok} onClose={() => setToast(null)} /> : null
  return { showToast, ToastEl }
}

// necesario para useToast
import React from 'react'
