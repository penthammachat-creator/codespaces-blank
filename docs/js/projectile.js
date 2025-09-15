(() => {
  // Elements
  const cnv = document.getElementById('canvas');
  const ctx = cnv.getContext('2d');
  const v0S = document.getElementById('v0');
  const thS = document.getElementById('theta');
  const gS  = document.getElementById('g');
  const v0Text = document.getElementById('v0Text');
  const thText = document.getElementById('thetaText');
  const gText  = document.getElementById('gText');
  const rVal = document.getElementById('rVal');
  const hVal = document.getElementById('hVal');
  const tVal = document.getElementById('tVal');
  const playBtn  = document.getElementById('play');
  const speedBtn = document.getElementById('speed');
  const resetBtn = document.getElementById('reset');

  // Presets
  document.getElementById('p0').onclick = () => setParams(12,25,9.81);
  document.getElementById('p1').onclick = () => setParams(20,45,9.81);
  document.getElementById('p2').onclick = () => setParams(14,70,9.81);
  document.getElementById('p3').onclick = () => setParams(12,45,1.62);

  // State
  let v0 = +v0S.value, theta = +thS.value, g = +gS.value;
  let running = false, currentTime = 0, speed = 1.0;
  let lastTs = 0;

  function setParams(v, th, gg){
    v0S.value = v; thS.value = th; gS.value = gg;
    onParamsChanged();
  }

  function onParamsChanged() {
    v0 = +v0S.value; theta = +thS.value; g = +gS.value;
    v0Text.textContent = `${v0.toFixed(0)} m/s`;
    thText.textContent = `${theta.toFixed(0)}°`;
    gText.textContent  = `${g.toFixed(2)} m/s²`;
    currentTime = 0; pause(); draw();
  }

  v0S.oninput = thS.oninput = gS.oninput = onParamsChanged;

  // Physics helpers
  const toRad = d => d*Math.PI/180;
  const timeOfFlight = () => (2*v0*Math.sin(toRad(theta)))/g;
  const range = () => (v0*v0*Math.sin(2*toRad(theta)))/g;
  const hMax = () => (Math.pow(v0*Math.sin(toRad(theta)),2))/(2*g);

  // Loop
  function tick(ts){
    if(!running){ return; }
    if(!lastTs) lastTs = ts;
    const dtSec = (ts - lastTs)/1000;
    lastTs = ts;
    currentTime += dtSec*speed;

    const T = timeOfFlight();
    if(currentTime >= T){
      currentTime = T;
      draw();
      pause();
      return;
    }
    draw();
    requestAnimationFrame(tick);
  }

  function play(){
    if(!running){
      running = true;
      playBtn.textContent = '⏸ Pause';
      lastTs = 0;
      requestAnimationFrame(tick);
    }
  }
  function pause(){
    if(running){
      running = false;
      playBtn.textContent = '▶ Play';
    }
  }

  playBtn.onclick = () => running ? pause() : play();
  speedBtn.onclick = () => {
    if(speed === 1.0){ speed = 0.25; speedBtn.textContent = 'Slow'; }
    else { speed = 1.0; speedBtn.textContent = 'Normal'; }
  };
  resetBtn.onclick = () => { setParams(12,25,9.81); };

  // Draw
  function draw(){
    // update info
    rVal.textContent = `${Math.max(0, range()).toFixed(2)} m`;
    hVal.textContent = `${Math.max(0, hMax()).toFixed(2)} m`;
    tVal.textContent = `${Math.max(0, timeOfFlight()).toFixed(2)} s`;

    ctx.clearRect(0,0,cnv.width,cnv.height);
    const groundY = cnv.height - 40;

    // ground
    ctx.strokeStyle = '#94a3b8';
    ctx.beginPath();
    ctx.moveTo(20,groundY);
    ctx.lineTo(cnv.width-20,groundY);
    ctx.stroke();

    // trajectory
    ctx.strokeStyle = '#d946ef';
    const scale = 10.0;
    const dt = 1/120;
    const th = toRad(theta);
    const T = timeOfFlight();

    let xPrev = 0, yPrev = 0;
    for(let t=0;t<=T+1e-6;t+=dt){
      const x = v0*Math.cos(th)*t;
      const y = v0*Math.sin(th)*t - 0.5*g*t*t;
      if(t>0){
        const sx1 = 40 + xPrev*scale;
        const sy1 = groundY - yPrev*scale;
        const sx2 = 40 + x*scale;
        const sy2 = groundY - y*scale;
        ctx.beginPath(); ctx.moveTo(sx1,sy1); ctx.lineTo(sx2,sy2); ctx.stroke();
      }
      xPrev = x; yPrev = y;
    }

    // moving dot
    const t = Math.max(0, Math.min(currentTime, T));
    const x = v0*Math.cos(th)*t;
    const y = v0*Math.sin(th)*t - 0.5*g*t*t;
    const cx = 40 + x*scale;
    const cy = groundY - y*scale;
    ctx.fillStyle = '#22d3ee';
    const r = 6; ctx.beginPath(); ctx.arc(cx,cy,r,0,Math.PI*2); ctx.fill();
  }

  onParamsChanged(); // init
})();
