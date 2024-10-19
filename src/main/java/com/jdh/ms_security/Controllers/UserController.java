package com.jdh.ms_security.Controllers;

import com.jdh.ms_security.Models.User;
import com.jdh.ms_security.Repositories.UserRepository;
import com.jdh.ms_security.Services.EncryptionService;
import com.jdh.ms_security.Services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository theuserRepository;

    @Autowired
    private EncryptionService theEncryptionService;

    @Autowired
    private NotificationService theNotificacionService;

    @GetMapping("")
    public List<User> find(){
        return this.theuserRepository.findAll();
    }

    @GetMapping("{id}")
    public User findById(@PathVariable String id){
        User theUser=this.theuserRepository.findById(id).orElse(null);
        return theUser;
    }
    @PostMapping("/create")
    public ResponseEntity<User>  create(@RequestBody User newUser){
        newUser.setPassword(this.theEncryptionService.convertSHA256(newUser.getPassword()));
        //Creamos una variable en donde guardamos al nuevo usuario ingresado y comprobamos si le llega el correo
        User userCreated = this.theuserRepository.save(newUser);
        theNotificacionService.SendlWelcome(userCreated.getEmail(), userCreated.getName());
        return new ResponseEntity<>(userCreated, HttpStatus.CREATED);
    }
    @PutMapping("{id}")
    public User update(@PathVariable String id, @RequestBody User newUser){
        User actualUser=this.theuserRepository.findById(id).orElse(null);
        if(actualUser!=null){
            actualUser.setName(newUser.getName());
            actualUser.setEmail(newUser.getEmail());
            actualUser.setPassword(this.theEncryptionService.convertSHA256(newUser.getPassword()));
            User updatedUser = this.theuserRepository.save(actualUser);
            theNotificacionService.SendUpdate(updatedUser.getEmail(), updatedUser.getName());
            return actualUser;
        }else{
            return null;
        }

    }
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        User theUser=this.theuserRepository.findById(id).orElse(null);
        theNotificacionService.SendDelete(theUser.getEmail(), theUser.getName());
        if (theUser!=null){
            this.theuserRepository.delete(theUser);
        }
    }
}
