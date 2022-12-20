package com.rogersillito.medialib;

import com.rogersillito.medialib.common.InstantiationTracingBeanPostProcessor;
import org.jaudiotagger.audio.AudioFileFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileFilter;

@Configuration
@SuppressWarnings("unused")
public class AppConfiguration {
    @Bean()
    public FileFilter audioFileFilter() {
        return new AudioFileFilter();
    }

    @Bean()
    public InstantiationTracingBeanPostProcessor instantiationTracingBeanPostProcessor() {
        return new InstantiationTracingBeanPostProcessor();
    }
}
