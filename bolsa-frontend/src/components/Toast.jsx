import { useEffect } from 'react'

export default function Toast({ msg, ok = true, onClose }) {
  useEffect(() => {
    const t = setTimeout(onClose, 3500)
    return () => clearTimeout(t)
  }, [onClose])

  return (
    <div className={ok ? 'alert-success' : 'alert-error'}
      style={{ position: 'fixed', top: 20, right: 20, zIndex: 9999, minWidth: 260, padding: '14px 18px' }}>
      {msg}
    </div>
  )
}
