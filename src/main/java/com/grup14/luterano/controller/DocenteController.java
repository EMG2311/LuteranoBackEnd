package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.DocenteException;
import com.grup14.luterano.request.docente.DocenteRequest;
import com.grup14.luterano.response.docente.DocenteResponse;
import com.grup14.luterano.service.DocenteService;
import com.grup14.luterano.service.implementation.DocenteServiceImpl;
import com.grup14.luterano.validation.MayorDeEdadGruoup;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/docente")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
@CrossOrigin(origins = "*")
public class DocenteController {
    @Autowired
    private DocenteService docenteService;

    @PostMapping("/create")
    @Operation(summary = "Crea un docente", description = "Crea un docente, tiene que si o si tener un mail de usuario creado previamente")
    public ResponseEntity<DocenteResponse> createDocente(@RequestBody@Validated({Default.class, MayorDeEdadGruoup.class}) DocenteRequest docenteRequest){
        try{
            return ResponseEntity.ok().body(docenteService.crearDocente(docenteRequest));
        }catch (DocenteException d){
            return ResponseEntity.status(422).body(docenteRequest.toResponse(d.getMessage(),-1));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(docenteRequest.toResponse("Error no controlado "+e.getMessage(),-2));
        }
    }


}
