float I0 = 0.1;
float S0 = 0.9;
float R0 = 0f;
float D0 = 0f;
float P = S0 + I0 + R0 + D0;
float[] i;
float[] s;
float[] r;
float[] d;

float b = 2f;
float l = 4f;
float m = 0f;

int maxT = 20;
float h = 0.1;

Control c;

PFont Arial;
PFont Consolas;

void settings() {
    size(650,420);
}

void setup() {
    Arial = createFont("Arial", 14, true);
    Consolas = createFont("Consolas", 14, true);
    
    c = new Control("Controle");
    
    surface.setResizable(true); 
    surface.setTitle("SIR Model");
    
    setVals();
}

void draw() {
  
    if(c == null) c = new Control("Controle");
  
    maxT = c.maxTime;
    b = c.beta;
    l = c.lambda;
    m = c.mu;
    I0 = c.i0;
    S0 = c.s0;
    R0 = c.r0;
    
    setVals();
  
    background(255);
    
    grid();
    
    noFill();
    strokeWeight(2);
    
    stroke(255,0,0);
    beginShape();
    for(int t = 0; t < round(maxT / h); t++){
        int x = round(map(t, 0, round(maxT / h), 30, width - 80));
        int y = round(i[t] * (height-30)) + 15;
        vertex(x, height - y);
    }
    endShape();
    
    fill(255, 0, 0);
    text("Infectés", width - 70, 15);
    noFill();
    
    stroke(0,255,0);
    beginShape();
    for(int t = 0; t < round(maxT / h); t++){
        int x = round(map(t, 0, round(maxT / h), 30, width - 80));
        int y = round(s[t] * (height-30)) + 15;
        vertex(x, height - y);
    }
    endShape();
    
    fill(0, 255, 0);
    text("Sains", width - 70, 30);
    noFill();
    
    stroke(0,0,255);
    beginShape();
    for(int t = 0; t < round(maxT / h); t++){
        int x = round(map(t, 0, round(maxT / h), 30, width - 80));
        int y = round(r[t] * (height-30)) + 15;
        vertex(x, height - y);
    }
    endShape();
    
    fill(0, 0, 255);
    text("Rétablis", width - 70, 45);
    noFill();
    
    stroke(0,0,0);
    beginShape();
    for(int t = 0; t < round(maxT / h); t++){
        int x = round(map(t, 0, round(maxT / h), 30, width - 80));
        int y = round(d[t] * (height-30)) + 15;
        vertex(x, height - y);
    }
    endShape();
    
    fill(0, 0, 0);
    text("Morts", width - 70, 60);
    noFill();
}

void grid() {
    stroke(200);
    strokeWeight(1);
    for(int t = 0; t <= maxT; t++) {
        int x = round(map(t, 0, maxT, 30, width - 80));
        line(x, 15, x, height - 15);
    }
    for(int n = 0; n <= 10; n++) {
        int y = round(map(n, 0, 10, 15, height - 15));
        line(30, y, width - 80, y);
        if (n > 0 && n < 10)
          text(str((10 - n) * 10), 5, y);
    }
    
    stroke(100);
    strokeWeight(2);
    line(30,15,30,height-15);
    line(30,height-15,width-80,height-15);
    
    fill(100);
    text("Temps (en j)", width - 85, height - 3);
    text("Part de la population (en %)", 3, 12);
    noFill();
}

void setVals () {
    i = new float[round(maxT / h)];
    s = new float[round(maxT / h)];
    r = new float[round(maxT / h)];
    d = new float[round(maxT / h)];
    i[0] = I0;
    s[0] = S0;
    r[0] = R0;
    d[0] = D0;
    for(int t = 1; t < round(maxT / h); t++) {
        i[t] = i[t-1] + h * ((b * i[t-1] * s[t-1]) - (i[t-1] / l) - (i[t-1] * m));
        i[t] = i[t] < 0 ? 0 : i[t];
        
        s[t] = s[t-1] - h * (b * i[t-1] * s[t-1]);
        s[t] = s[t] < 0 ? 0 : s[t];
        
        r[t] = r[t-1] + h * (i[t-1] / l);
        r[t] = r[t] < 0 ? 0 : r[t];
        
        d[t] = d[t-1] + h * (i[t-1] * m);
        d[t] = d[t] < 0 ? 0 : d[t];
    }
}
