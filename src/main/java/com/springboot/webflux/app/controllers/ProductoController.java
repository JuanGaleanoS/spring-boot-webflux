package com.springboot.webflux.app.controllers;

import com.springboot.webflux.app.models.dao.ProductoDao;
import com.springboot.webflux.app.models.documents.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Controller
public class ProductoController {

    @Autowired
    private ProductoDao productoDao;

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @GetMapping({"/", "/listar"})
    public String listar(Model model) {

        Flux<Producto> productoFlux = productoDao.findAll() // se subscribe de forma automática
                .map(producto -> {
                    producto.setNombre(producto.getNombre().toLowerCase());
                    return producto;
                });
        productoFlux.subscribe(producto -> log.info(producto.getNombre()));

        model.addAttribute("productos", productoFlux);
        model.addAttribute("titulo", "Listado de productos");

        return "listar";
    }

    @GetMapping("/listar-data-driven")
    public String listarDataDriven(Model model) {

        Flux<Producto> productoFlux = productoDao.findAll() // se subscribe de forma automática
                .map(producto -> {
                    producto.setNombre(producto.getNombre().toLowerCase());
                    return producto;
                }).delayElements(Duration.ofSeconds(1));

        productoFlux.subscribe(producto -> log.info(producto.getNombre()));

        model.addAttribute("productos", new ReactiveDataDriverContextVariable(productoFlux, 2));
        model.addAttribute("titulo", "Listado de productos");

        return "listar";
    }

    @GetMapping("/listar-full")
    public String listarFull(Model model) {

        Flux<Producto> productoFlux = productoDao.findAll() // se subscribe de forma automática
                .map(producto -> {
                    producto.setNombre(producto.getNombre().toLowerCase());
                    return producto;
                }).repeat(5000);

        model.addAttribute("productos", productoFlux);
        model.addAttribute("titulo", "Listado de productos");

        return "listar";
    }

    @GetMapping("/listar-chunked")
    public String listarChunked(Model model) {

        Flux<Producto> productoFlux = productoDao.findAll() // se subscribe de forma automática
                .map(producto -> {
                    producto.setNombre(producto.getNombre().toLowerCase());
                    return producto;
                }).repeat(5000);
        // spring.thymeleaf.reactive.max-chunk-size=1024 defino que cada que el buffer llegue a 1 byte se enviará info a la vista
        // El parámetro spring.thymeleaf.reactive.chunked-mode-view-names=listar-chunked permite que solo a esta vista le aplique la configuración de chunk

        model.addAttribute("productos", productoFlux);
        model.addAttribute("titulo", "Listado de productos");

        return "listar-chunked";
    }

}
