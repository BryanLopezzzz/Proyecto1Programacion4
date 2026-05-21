export default function BarraCoincidencia({ pct }) {
  return (
    <div className="bar-wrap">
      <div className="bar-outer">
        <div className="bar-inner" style={{ width: `${pct}%` }} />
      </div>
      <span className="bar-label">{pct.toFixed(1)}%</span>
    </div>
  )
}
