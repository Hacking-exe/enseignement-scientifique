import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SIR_infectiology_model extends PApplet {

float I0 = 0.1f;
float S0 = 0.9f;
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
float h = 0.1f;

Control c;

PFont Arial;
PFont Consolas;

public void settings() {
    size(650,420);
}

public void setup() {
    Arial = createFont("Arial", 14, true);
    Consolas = createFont("Consolas", 14, true);
    
    c = new Control("Controle");
    
    surface.setResizable(true); 
    surface.setTitle("SIR Model");
    
    setVals();
}

public void draw() {
  
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

public void grid() {
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

public void setVals () {
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
class Control extends PApplet {
  
    Control(String name) {
        this.name = name;
        PApplet.runSketch(new String[]{this.getClass().getName()}, this);
    }
    
    String name;
    
    HScrollbar maxTime_;
    int maxTime = 20;
    
    HScrollbar beta_;
    float beta = .5f;
    
    HScrollbar lambda_;
    int lambda = 4;
    
    HScrollbar mu_;
    float mu = 0.1f;
    
    HScrollbar[] pops;
    
    float i0 = 0.1f;
    float s0 = 0.9f;
    float r0 = 0f;
    
    public void settings() {
        size(400,290);
    }
    
    public void setup() {
        surface.setTitle(name);
      
        maxTime_ = new HScrollbar(10f, 50f, this.width-20, 10, 1);
        beta_ = new HScrollbar(10f, maxTime_.ypos + maxTime_.sheight + 30, this.width-20, 10, 1, 0.2f);
        lambda_ = new HScrollbar(10f, beta_.ypos + beta_.sheight + 30, this.width-20, 10, 1, 0.07f);
        mu_ = new HScrollbar(10f, lambda_.ypos + lambda_.sheight + 30, this.width-20, 10, 1, 0);
        pops = new HScrollbar[3];
        pops[0] = new HScrollbar(10f, mu_.ypos + mu_.sheight + 30, this.width-20, 10, 1, 0.1f);
        pops[1] = new HScrollbar(10f, pops[0].ypos + pops[0].sheight + 30, this.width-20, 10, 1, 0.9f);
        pops[2] = new HScrollbar(10f, pops[1].ypos + pops[1].sheight + 30, this.width-20, 10, 1, 0f);
    }
    
    public void draw() {
        
        background(255);
        
        maxTime_.update();
        maxTime_.display();
        beta_.update();
        beta_.display();
        lambda_.update();
        lambda_.display();
        mu_.update();
        mu_.display();
        
        for(HScrollbar pop : pops) {
            pop.update();
            
            if(pop.spos >= pop.sposMax) {
                for(HScrollbar pop2 : pops) {
                    if (pop2 != pop) {
                        pop2.newspos = pop2.sposMin;
                    }
                }
            }
            else if(pop.moved) {
                int div = pops.length - 1;
                float dp = pop.dp;
                
                for(HScrollbar pop2 : pops) {
                    if (pop2 != pop) {
                        pop2.newspos -= dp / (pop2.loose * div);
                        pop2.spos = pop2.newspos;
                    }
                    if (pop2.getPos() < 0) {
                        pop2.newspos = pop2.sposMin;
                        pop2.spos = pop2.newspos;
                        
                        for(HScrollbar pop3 : pops){
                            if (pop3 != pop && pop3 != pop2) {
                                pop3.newspos -= dp / (pop3.loose * div);
                                pop3.spos = pop3.newspos;
                            }
                        }
                    }
                }
            }
        }
        
        for(HScrollbar pop : pops)
            pop.display();
        
        maxTime = maxTime_.getVal(10,30);
        beta = beta_.getVal(0,1000);
          beta *= 0.005f;
        lambda = lambda_.getVal(1,50);
        mu = mu_.getVal(0,1000);
          mu *= 0.002f;
        i0 = pops[0].getVal(0,100);
          i0 *= 0.01f;
        s0 = pops[1].getVal(0,100);
          s0 *= 0.01f;
        r0 = pops[2].getVal(0,100);
          r0 *= 0.01f;
        
        textAlign(CENTER, CENTER);
        fill(0);
        textFont(Consolas, 30);
        text("Panneau de controle", width/2, 15);
        
        textFont(Arial, 14);
        text("Nb de jours affichés: " + str(maxTime), width/2, maxTime_.ypos + maxTime_.sheight + 10);
        text("Taux d'incidence β: " + str(round(beta * 100)) + "%", width/2, beta_.ypos + beta_.sheight + 10);
        text("Temps de guérison λ: " + str(lambda) + " jours", width/2, lambda_.ypos + lambda_.sheight + 10);
        text("Taux de mortalité µ: " + str(round(mu * 100)) + "%", width/2, mu_.ypos + mu_.sheight + 10);
        text("Population infectée initiale Iₒ: " + str(round(i0 * 100)) + "%", width/2, (int)(pops[0].ypos + pops[0].sheight + 10));
        text("Population saine initiale Sₒ:" + str(round(s0 * 100)) + "%", width/2, (int)(pops[1].ypos + pops[1].sheight + 10));
        text("Population rétablie initiale Rₒ (taux de vaccination): " + str(round(r0 * 100)) + "%", width/2, (int)(pops[2].ypos + pops[2].sheight + 10));
    }
    
    public void exit() {
        dispose();
        c = null;
    }
    
    
  
    
    
    // Scroller sub-class
    
    class HScrollbar {
        int swidth, sheight;    // width and height of bar
        float xpos, ypos;       // x and y position of bar
        float spos, newspos;    // x position of slider
        float sposMin, sposMax; // max and min values of slider
        int loose;              // how loose/heavy
        boolean over;           // is the mouse over the slider?
        boolean locked;
        float ratio;
        boolean moved = false;
        float dp;
      
        HScrollbar (float xp, float yp, int sw, int sh, int l) {
            swidth = sw;
            sheight = sh;
            int widthtoheight = sw - sh;
            ratio = (float)sw / (float)widthtoheight;
            xpos = xp;
            ypos = yp-sheight/2;
            spos = xpos + swidth/2 - sheight/2;
            newspos = spos;
            sposMin = xpos;
            sposMax = xpos + swidth - sheight;
            loose = l;
        }
        
        HScrollbar (float xp, float yp, int sw, int sh, int l, float init) {
            swidth = sw;
            sheight = sh;
            int widthtoheight = sw - sh;
            ratio = (float)sw / (float)widthtoheight;
            xpos = xp;
            ypos = yp-sheight/2;
            spos = xpos + swidth * init - sheight * init;
            newspos = spos;
            sposMin = xpos;
            sposMax = xpos + swidth - sheight;
            loose = l;
        }
      
        public void update() {
            if (overEvent()) {
                over = true;
            } else {
                over = false;
            }
            if (mousePressed && over) {
                locked = true;
            }
            if (!mousePressed) {
                locked = false;
            }
            if (locked) {
                newspos = constrain(mouseX-sheight/2, sposMin, sposMax);
            }
            if (abs(newspos - spos) > 1) {
                dp = newspos - spos;
                spos += dp/loose;
                moved = true;
            } else {moved = false;}
        }
      
        //float constrain(float val, float minv, float maxv) {
        //  return min(max(val, minv), maxv);
        //}
      
        public boolean overEvent() {
            if (mouseX > xpos && mouseX < xpos+swidth &&
                 mouseY > ypos && mouseY < ypos+sheight) {
                return true;
            } else {
                return false;
            }
        }
      
        public void display() {
            noStroke();
            fill(204);
            rect(xpos, ypos, swidth, sheight, sheight/2);
            if (over || locked) {
                fill(0, 0, 0);
            } else {
                fill(102, 102, 102);
            }
            rect(spos, ypos, sheight, sheight, sheight/2);
        }
      
        public float getPos() {
            // Convert spos to be values between
            // 0 and the total width of the scrollbar
            return (spos - xpos) / (swidth - sheight);
        }
        
        public float getVal(float min, float max) {
            return map(getPos(), 0, 1, min, max);
        }
        
        public int getVal(int min, int max) {
            return round(map(getPos(), 0, 1, min, max));
        }   
    }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SIR_infectiology_model" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
