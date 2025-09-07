
package com.test.tms;

import com.test.tms.models.Translation;
import com.test.tms.repositories.TranslationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootApplication
public class TranslationManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranslationManagementServiceApplication.class, args);
    }
}
