package com.shotx.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShopApplication {
    //	@Value("${stripe.api.key}")
//	private static String stripeApiKey;
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);

    }


}
