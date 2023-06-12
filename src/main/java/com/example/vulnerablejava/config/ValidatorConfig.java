// package com.example.vulnerablejava.config;

// import javax.validation.Validation;
// import javax.validation.Validator;
// import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class ValidatorConfig {

//     /**
//      * 禁止EL解析，修复EL注入漏洞
//      * @return
//      */
//     @Bean
//     public Validator validator() {
//         Validator validator = Validation.byDefaultProvider()
//                                         .configure()
//                                         .messageInterpolator(new ParameterMessageInterpolator())
//                                         .buildValidatorFactory()
//                                         .getValidator();
//         return validator;
//     }
// }
