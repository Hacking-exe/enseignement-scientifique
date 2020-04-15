class Control extends PApplet {
  
    Control(String name) {
        this.name = name;
        PApplet.runSketch(new String[]{this.getClass().getName()}, this);
    }
    
    String name;
    
    HScrollbar maxTime_;
    int maxTime = 20;
    
    HScrollbar beta_;
    float beta = .5;
    
    HScrollbar lambda_;
    int lambda = 4;
    
    HScrollbar mu_;
    float mu = 0.1;
    
    HScrollbar[] pops;
    
    float i0 = 0.1;
    float s0 = 0.9;
    float r0 = 0f;
    
    public void settings() {
        size(400,290);
    }
    
    public void setup() {
        surface.setTitle(name);
      
        maxTime_ = new HScrollbar(10f, 50f, this.width-20, 10, 1);
        beta_ = new HScrollbar(10f, maxTime_.ypos + maxTime_.sheight + 30, this.width-20, 10, 1, 0.2);
        lambda_ = new HScrollbar(10f, beta_.ypos + beta_.sheight + 30, this.width-20, 10, 1, 0.07);
        mu_ = new HScrollbar(10f, lambda_.ypos + lambda_.sheight + 30, this.width-20, 10, 1, 0);
        pops = new HScrollbar[3];
        pops[0] = new HScrollbar(10f, mu_.ypos + mu_.sheight + 30, this.width-20, 10, 1, 0.1);
        pops[1] = new HScrollbar(10f, pops[0].ypos + pops[0].sheight + 30, this.width-20, 10, 1, 0.9);
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
          beta *= 0.005;
        lambda = lambda_.getVal(1,50);
        mu = mu_.getVal(0,1000);
          mu *= 0.002;
        i0 = pops[0].getVal(0,100);
          i0 *= 0.01;
        s0 = pops[1].getVal(0,100);
          s0 *= 0.01;
        r0 = pops[2].getVal(0,100);
          r0 *= 0.01;
        
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
    
    void exit() {
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
      
        void update() {
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
      
        boolean overEvent() {
            if (mouseX > xpos && mouseX < xpos+swidth &&
                 mouseY > ypos && mouseY < ypos+sheight) {
                return true;
            } else {
                return false;
            }
        }
      
        void display() {
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
      
        float getPos() {
            // Convert spos to be values between
            // 0 and the total width of the scrollbar
            return (spos - xpos) / (swidth - sheight);
        }
        
        float getVal(float min, float max) {
            return map(getPos(), 0, 1, min, max);
        }
        
        int getVal(int min, int max) {
            return round(map(getPos(), 0, 1, min, max));
        }   
    }
}
