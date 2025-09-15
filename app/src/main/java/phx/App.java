package phx;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class App extends Application {
    private double v0 = 12.0, theta = 25.0, g = 9.81; // เริ่มต้น
    private Label rLabel = new Label(), hLabel = new Label(), tLabel = new Label();
    private Canvas canvas = new Canvas(900, 420);

    @Override public void start(Stage stage) {
        Slider v0S = slider(5,30,v0,1), thS = slider(0,80,theta,1), gS = slider(1.62,10,g,0.01);
        Label v0Val = new Label(); v0Val.textProperty().bind(v0S.valueProperty().asString("%.0f m/s"));
        Label thVal = new Label(); thVal.textProperty().bind(thS.valueProperty().asString("%.0f°"));
        Label gVal = new Label(); gVal.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("%.2f m/s²", gS.getValue()), gS.valueProperty()));
        HBox controls = row(
            col("ความเร็วต้น", v0S, v0Val),
            col("มุมยิง", thS, thVal),
            col("g", gS, gVal),
            presets(v0S, thS, gS),
            resetBar(v0S, thS, gS)
        );
        VBox info = new VBox(10, title("ค่าคำนวณ (สด)"), big("R: ", rLabel), big("H: ", hLabel), big("T: ", tLabel));
        info.setPadding(new Insets(16)); info.setPrefWidth(220);
        info.setStyle("-fx-background-color:#0f172a; -fx-background-radius:16;");
        info.getChildren().forEach(n -> n.setStyle("-fx-text-fill:white;"));
        BorderPane root = new BorderPane(canvas, null, info, controls, null);
        BorderPane.setMargin(canvas,new Insets(8)); BorderPane.setMargin(info,new Insets(8)); BorderPane.setMargin(controls,new Insets(8));
        v0S.valueProperty().addListener((o,a,n)->{ v0=n.doubleValue(); refresh(); });
        thS.valueProperty().addListener((o,a,n)->{ theta=n.doubleValue(); refresh(); });
        gS.valueProperty().addListener((o,a,n)->{ g=n.doubleValue(); refresh(); });
        refresh();
        stage.setTitle("PHX — Projectile (Lab 1)"); stage.setScene(new Scene(root, 1140, 560)); stage.show();
    }
    private void refresh(){
        double th = Math.toRadians(theta);
        double R = (v0*v0*Math.sin(2*th))/g, H = (Math.pow(v0*Math.sin(th),2))/(2*g), T = (2*v0*Math.sin(th))/g;
        rLabel.setText(String.format("%.2f m", Math.max(0,R)));
        hLabel.setText(String.format("%.2f m", Math.max(0,H)));
        tLabel.setText(String.format("%.2f s", Math.max(0,T)));
        GraphicsContext g2 = canvas.getGraphicsContext2D();
        g2.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        double groundY = canvas.getHeight()-40; g2.strokeLine(20,groundY,canvas.getWidth()-20,groundY);
        g2.setStroke(javafx.scene.paint.Color.MAGENTA);
        double scale=10, dt=1.0/120.0, xPrev=0, yPrev=0, th0=Math.toRadians(theta);
        for(double t=0; t<=T; t+=dt){
            double x=v0*Math.cos(th0)*t, y=v0*Math.sin(th0)*t-0.5*g*t*t;
            if(t>0){ g2.strokeLine(40+xPrev*scale, groundY-yPrev*scale, 40+x*scale, groundY-y*scale); }
            xPrev=x; yPrev=y;
        }
    }
    private Slider slider(double min,double max,double val,double major){ var s=new Slider(min,max,val); s.setShowTickMarks(true); s.setShowTickLabels(true); s.setMajorTickUnit(major); return s; }
    private VBox col(String label, Slider s, Label v){ var l=new Label(label); var box=new VBox(4,l,s,v); box.setPadding(new Insets(0,12,0,12)); return box; }
    private HBox row(javafx.scene.Node... nodes){ var h=new HBox(10,nodes); h.setAlignment(Pos.CENTER_LEFT); return h; }
    private VBox presets(Slider v0, Slider th, Slider g){
        Button b1=new Button("พื้นฐาน"); b1.setOnAction(e->{ v0.setValue(12); th.setValue(25); g.setValue(9.81); });
        Button b2=new Button("ไกลสุด (45°)"); b2.setOnAction(e->{ v0.setValue(20); th.setValue(45); g.setValue(9.81); });
        Button b3=new Button("วิถีชัน (70°)"); b3.setOnAction(e->{ v0.setValue(14); th.setValue(70); g.setValue(9.81); });
        Button b4=new Button("ดวงจันทร์"); b4.setOnAction(e->{ v0.setValue(12); th.setValue(45); g.setValue(1.62); });
        return new VBox(4,new Label("Presets"), new HBox(6,b1,b2,b3,b4));
    }
    private HBox resetBar(Slider v0, Slider th, Slider g){
        Button reset=new Button("Reset"); reset.setOnAction(e->{ v0.setValue(12); th.setValue(25); g.setValue(9.81); });
        var hb=new HBox(8,reset); hb.setAlignment(Pos.CENTER_LEFT); hb.setPadding(new Insets(0,12,0,12)); return hb;
    }
    private Label title(String s){ var l=new Label(s); l.setFont(Font.font(18)); return l; }
    private HBox big(String p, Label v){ var lp=new Label(p); lp.setFont(Font.font(16)); v.setFont(Font.font(16)); var hb=new HBox(6,lp,v); hb.setAlignment(Pos.CENTER_LEFT); return hb; }
    public static void main(String[] args){ launch(); }
}
