package xyz.iwolfking.woldsvaults.configs;

import iskallia.vault.config.BingoConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BallisticBingoConfig extends BingoConfig {

    public BallisticBingoConfig() {
    }

    @Override
    public String getName() {
        return "ballistic_bingo";
    }

    @Override
    protected void reset() {
        try (InputStream stream = this.getClass().getResourceAsStream("/default_configs/ballistic_bingo.json")) {
            if(stream == null) {
                throw new IOException();
            }

            BallisticBingoConfig config = this.getGson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), BallisticBingoConfig.class);
            this.pools = config.pools;
        } catch (IOException e) {
            System.out.println("Failed to read default Ballistic Bingo config...");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeConfig() throws IOException {
        File file = this.getConfigFile();
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            return;
        }

        try (InputStream stream = this.getClass().getResourceAsStream("/default_configs/ballistic_bingo.json")) {
            if (stream == null) {
                throw new IOException("Missing bundled Ballistic Bingo config");
            }

            try (FileOutputStream output = new FileOutputStream(file)) {
                stream.transferTo(output);
            }
        }
    }


}
