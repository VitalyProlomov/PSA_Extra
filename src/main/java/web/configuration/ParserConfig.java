package web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pokerlibrary.parsers.gg.GGPokerokHoldem9MaxParser;

@Configuration
public class ParserConfig {

    @Bean
    public GGPokerokHoldem9MaxParser ggPokerokHoldem9MaxParser() {
        return new GGPokerokHoldem9MaxParser();
    }
}