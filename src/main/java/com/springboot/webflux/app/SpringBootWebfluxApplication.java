package com.springboot.webflux.app;

import com.springboot.webflux.app.models.dao.ProductoDao;
import com.springboot.webflux.app.models.documents.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

    @Autowired
    private ProductoDao dao;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebfluxApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        mongoTemplate.dropCollection("productos").subscribe();

        Flux.just(
                        new Producto("TV", 1800),
                        new Producto("LAVADORA", 800),
                        new Producto("PORTATIL", 2500),
                        new Producto("MICROONDAS", 100),
                        new Producto("CAFETERA", 100),
                        new Producto("MOUSE", 50),
                        new Producto("TECLADO", 100),
                        new Producto("CELULAR", 3000),
                        new Producto("CONTROL TV", 20)
                )
                //FlatMap permite recibir un observable y retornar un tipo objeto Producto
                .flatMap(producto -> {
                    producto.setCreateAt(new Date());
                    return dao.save(producto);
                })
                .subscribe(producto -> log.info(
                        String.format("Producto insertado %s / %s", producto.getId(), producto.getNombre())
                ));
    }
}
