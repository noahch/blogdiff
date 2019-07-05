package ch.uzh.seal.BLogDiff.preprocessing;

import ch.uzh.seal.BLogDiff.model.rest.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@EnableConfigurationProperties(value = PreprocessorConfig.class)
public class PreprocessorHandler {

    @Autowired
    private PreprocessorConfig preprocessorConfig;

    public String preprocessLog(String buildLog) {

        Log bLog = new Log();
        bLog.setContent(buildLog);
        preprocessorConfig.getPreprocessors().forEach(s ->{
//            log.info(s);
            try {
                Class p = Class.forName(s);
                Object o = p.getDeclaredConstructor().newInstance();
                Preprocessor pre = (Preprocessor)o;
                bLog.setContent(pre.process(bLog.getContent(), preprocessorConfig.getReplacement()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return bLog.getContent();
    }
}
