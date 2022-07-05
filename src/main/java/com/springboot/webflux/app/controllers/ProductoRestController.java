package com.springboot.webflux.app.controllers;

import com.springboot.webflux.app.models.dao.ProductoDao;
import com.springboot.webflux.app.models.documents.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    @Autowired
    private ProductoDao productoDao;

    private static final Logger log = LoggerFactory.getLogger(ProductoRestController.class);

    @GetMapping
    public Flux<Producto> index(Model model) {
        return productoDao.findAll() // se subscribe de forma automática
                .map(producto -> {
                    producto.setNombre(producto.getNombre().toLowerCase());
                    return producto;
                }).doOnNext(producto -> log.info(producto.getNombre()));
    }

    @GetMapping("/{id}")
    public Mono<Producto> show(@PathVariable String id) {
        /*return productoDao.findById(id) // se subscribe de forma automática
                .map(producto -> {
                    producto.setNombre(producto.getNombre().toLowerCase());
                    return producto;
                }).doOnNext(producto -> log.info(producto.getNombre()));*/
        Flux<Producto> productoFlux = productoDao.findAll();
        return productoFlux
                .filter(producto -> producto.getId().equals(id))
                .next() // emite solo el primer elemento dentro del flux conviertiendolo en Mono
                .doOnNext(producto -> log.info(producto.getNombre()));
    }

}
