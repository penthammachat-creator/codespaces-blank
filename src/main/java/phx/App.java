package phx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class App extends Application {

    // state (parameters)
    private double v0 = 12.0;     // m/s
    private double theta = 25.0;  // degrees
    private double g = 9.81;      // m/s^2

    // UI labels
    private final Label rLabel = new Label();
    private final Label hLabel = new Label();
    private final Label tLabel = new Label();

    // canvas
    private final Canvas canvas = new Canvas(900, 420);

    // animation
    private final AnimationTimer timer = new AnimationTimer() {
        private long lastNs = -1L;

        @Override
        public void handle(long now) {
            if (lastNs < 0L) {
                lastNs = now;
                return;
            }
            double elapsed = (now - lastNs) / 1_000_000_000.0;
            lastNs = now;

            // speedMultiplier: 1.0 = Normal, 0.25 = Slow
            currentTime += elapsed * speedMultiplier;

            double totalT = totalFlightTime();
            if (currentTime >= totalT) {
                currentTime = totalT;
                draw();      // final frame
                pause();     // auto-stop at the end
                return;
            }
            draw();
        }
    };

    private boolean running = false;
    private double currentTime = 0.0;
    private double speedMultiplier = 1.0; // Normal = 1.0, Slow = 0.25

    // controls we need to access in code
    private Button playPauseBtn;
    private Button speedBtn;

    @Override
    public void start(Stage stage) {
        // sliders
        Slider v0S = slider(5, 30, v0, 1);
        Slider thS = slider(0, 80, theta, 1);
        Slider gS = slider(1.62, 10.0, g, 0.01);

        Label v0Val = new Label();
        v0Val.textProperty().bind(v0S.valueProperty().asString("%.0f m/s"));

        Label thVal = new Label();
        thVal.textProperty().bind(thS.valueProperty().asString("%.0f°"));

        Label gVal = new Label();
        gVal.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("%.2f m/s²", gS.getValue()),
                gS.valueProperty()));

        // transport (▶/⏸ + Slow/Normal + Reset)
        playPauseBtn = new Button("▶ Play");
        playPauseBtn.setOnAction(e -> {
            if (running) {
                pause();
            } else {
                play();
            }
        });

        speedBtn = new Button("Normal");
        speedBtn.setOnAction(e -> {
            if (speedMultiplier == 1.0) {
                speedMultiplier = 0.25;
                speedBtn.setText("Slow");
            } else {
                speedMultiplier = 1.0;
                speedBtn.setText("Normal");
            }
        });

        Button resetBtn = new Button("Reset");
        resetBtn.setOnAction(e -> {
            v0S.setValue(12);
            thS.setValue(25);
            gS.setValue(9.81);
            currentTime = 0.0;
            pause();
            draw();
        });

        HBox transport = new HBox(8, playPauseBtn, speedBtn, resetBtn);
        transport.setAlignment(Pos.CENTER_LEFT);
        transport.setPadding(new Insets(0, 12, 0, 12));

        // controls row
        HBox controls = row(
                col("ความเร็วต้น", v0S, v0Val),
                col("มุมยิง", thS, thVal),
                col("g", gS, gVal),
                presets(v0S, thS, gS),
                transport
        );

        // info card
        VBox info = new VBox(
                10,
                title("ค่าคำนวณ (สด)"),
                big("R: ", rLabel),
                big("H: ", hLabel),
                big("T: ", tLabel)
        );
        info.setPadding(new Insets(16));
        info.setPrefWidth(220);
        info.setStyle("-fx-background-color:#0f172a; -fx-background-radius:16;");
        info.getChildren().forEach(n -> n.setStyle("-fx-text-fill:white;"));

        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setRight(info);
        root.setBottom(controls);
        BorderPane.setMargin(canvas, new Insets(8));
        BorderPane.setMargin(info, new Insets(8));
        BorderPane.setMargin(controls, new Insets(8));

        // sliders events: เมื่อแก้พารามิเตอร์ ให้หยุดเล่นและวาดใหม่
        v0S.valueProperty().addListener((o, a, nv) -> {
            v0 = nv.doubleValue();
            currentTime = 0.0;
            pause();
            draw();
        });
        thS.valueProperty().addListener((o, a, nv) -> {
            theta = nv.doubleValue();
            currentTime = 0.0;
            pause();
            draw();
        });
        gS.valueProperty().addListener((o, a, nv) -> {
            g = nv.doubleValue();
            currentTime = 0.0;
            pause();
            draw();
        });

        draw();

        stage.setTitle("PHX — Projectile (Lab 1)");
        stage.setScene(new Scene(root, 1140, 560));
        stage.show();
    }

    // --- animation helpers ---
    private void play() {
        if (!running) {
            running = true;
            playPauseBtn.setText("⏸ Pause");
            timer.start();
        }
    }

    private void pause() {
        if (running) {
            running = false;
            timer.stop();
            playPauseBtn.setText("▶ Play");
        }
    }

    // --- physics ---
    private double totalFlightTime() {
        double th = Math.toRadians(theta);
        return (2 * v0 * Math.sin(th)) / g;
    }

    private double range() {
        double th = Math.toRadians(theta);
        return (v0 * v0 * Math.sin(2 * th)) / g;
    }

    private double hmax() {
        double th = Math.toRadians(theta);
        return (Math.pow(v0 * Math.sin(th), 2)) / (2 * g);
    }

    private void draw() {
        // update info
        rLabel.setText(String.format("%.2f m", Math.max(0, range())));
        hLabel.setText(String.format("%.2f m", Math.max(0, hmax())));
        tLabel.setText(String.format("%.2f s", Math.max(0, totalFlightTime())));

        GraphicsContext g2 = canvas.getGraphicsContext2D();
        g2.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double groundY = canvas.getHeight() - 40;
        g2.setStroke(Color.BLACK);
        g2.strokeLine(20, groundY, canvas.getWidth() - 20, groundY);

        // draw full trajectory (เส้นสีม่วง)
        g2.setStroke(Color.MAGENTA);
        double scale = 10.0;
        double dt = 1.0 / 120.0;
        double th = Math.toRadians(theta);
        double totalT = totalFlightTime();

        double xPrev = 0.0;
        double yPrev = 0.0;
        for (double t = 0.0; t <= totalT; t += dt) {
            double x = v0 * Math.cos(th) * t;
            double y = v0 * Math.sin(th) * t - 0.5 * g * t * t;
            if (t > 0.0) {
                double sx1 = 40 + xPrev * scale;
                double sy1 = groundY - yPrev * scale;
                double sx2 = 40 + x * scale;
                double sy2 = groundY - y * scale;
                g2.strokeLine(sx1, sy1, sx2, sy2);
            }
            xPrev = x;
            yPrev = y;
        }

        // moving ball at currentTime
        double t = Math.max(0.0, Math.min(currentTime, totalT));
        double x = v0 * Math.cos(th) * t;
        double y = v0 * Math.sin(th) * t - 0.5 * g * t * t;
        double cx = 40 + x * scale;
        double cy = groundY - y * scale;

        g2.setFill(Color.DARKCYAN);
        double r = 6.0;
        g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
    }

    // --- small UI helpers ---
    private Slider slider(double min, double max, double val, double major) {
        Slider s = new Slider(min, max, val);
        s.setShowTickMarks(true);
        s.setShowTickLabels(true);
        s.setMajorTickUnit(major);
        return s;
    }

    private VBox col(String label, Slider s, Label val) {
        Label l = new Label(label);
        VBox box = new VBox(4, l, s, val);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 12, 0, 12));
        return box;
    }

    private HBox row(javafx.scene.Node... nodes) {
        HBox hbox = new HBox(10, nodes);
        hbox.setAlignment(Pos.CENTER_LEFT);
        return hbox;
    }

    private VBox presets(Slider v0S, Slider thS, Slider gS) {
        Button b1 = new Button("พื้นฐาน");
        b1.setOnAction(e -> {
            v0S.setValue(12);
            thS.setValue(25);
            gS.setValue(9.81);
            currentTime = 0.0;
            pause();
            draw();
        });

        Button b2 = new Button("ไกลสุด (45°)");
        b2.setOnAction(e -> {
            v0S.setValue(20);
            thS.setValue(45);
            gS.setValue(9.81);
            currentTime = 0.0;
            pause();
            draw();
        });

        Button b3 = new Button("วิถีชัน (70°)");
        b3.setOnAction(e -> {
            v0S.setValue(14);
            thS.setValue(70);
            gS.setValue(9.81);
            currentTime = 0.0;
            pause();
            draw();
        });

        Button b4 = new Button("ดวงจันทร์");
        b4.setOnAction(e -> {
            v0S.setValue(12);
            thS.setValue(45);
            gS.setValue(1.62);
            currentTime = 0.0;
            pause();
            draw();
        });

        HBox row = new HBox(6, b1, b2, b3, b4);
        VBox box = new VBox(4, new Label("Presets"), row);
        box.setPadding(new Insets(0, 12, 0, 12));
        return box;
    }

    private Label title(String s) {
        Label l = new Label(s);
        l.setFont(Font.font(18));
        return l;
    }

    private HBox big(String prefix, Label value) {
        Label p = new Label(prefix);
        p.setFont(Font.font(16));
        value.setFont(Font.font(16));
        HBox hb = new HBox(6, p, value);
        hb.setAlignment(Pos.CENTER_LEFT);
        return hb;
    }

    public static void main(String[] args) {
        launch();
    }
}
