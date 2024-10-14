package com.example.application.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import com.example.application.controller.PersonController;
import com.example.application.data.vo.v1.PersonVO;
import com.example.application.data.vo.v2.PersonVOV2;
import com.example.application.exceptions.RequiredObjectIsNullException;
import com.example.application.exceptions.ResourceNotFoundException;
import com.example.application.mapper.DozerMapper;
import com.example.application.mapper.custom.PersonMapper;
import com.example.application.model.Person;
import com.example.application.repositories.PersonRepository;

import jakarta.transaction.Transactional;


@Service
public class PersonServices {

    @Autowired
    PersonRepository repository;

    @Autowired
    PersonMapper mapper;
    
    @Autowired
    PagedResourcesAssembler<PersonVO> assembler;
    
    public PersonVO create(PersonVO person) {
        
        if(person == null) throw new RequiredObjectIsNullException();

        var entity = DozerMapper.parseObject(person, Person.class); 
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public PersonVOV2 createV2(PersonVOV2 person) {
        var entity = mapper.convertVoToEntity(person); 
        var vo = mapper.convertEntityToVo(repository.save(entity));

        return vo;
    }
    
    public PersonVO update(PersonVO person) {

        if(person == null) throw new RequiredObjectIsNullException();

        var entity = repository.findById(person.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());

        return vo;
    }    
    
    public void delete(Long id) {
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }
    
    public PersonVO findById(Long id) {
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        var vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
    }

    @Transactional
    public PersonVO disablePerson(Long id) {
        repository.disablePerson(id);
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        var vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
    }
    
    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {
        var personPage = repository.findAll(pageable);
        var personsVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
        personsVosPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
        Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
        return assembler.toModel(personsVosPage, link);
    }
    public PagedModel<EntityModel<PersonVO>> findPersonsByName(String firstName, Pageable pageable) {
        var personPage = repository.findPersonsByName(firstName, pageable);
        var personsVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
        personsVosPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
        Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
        return assembler.toModel(personsVosPage, link);
    }
}