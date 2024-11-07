package com.ram.venga.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ram.venga.domain.CadeauxBA;
import com.ram.venga.model.enumeration.CategorieEntiteEnum;
import com.ram.venga.repos.CadeauxBARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ram.venga.domain.CategorieCadeau;
import com.ram.venga.mapper.CategorieCadeauMapper;
import com.ram.venga.model.CategorieCadeauDTO;
import com.ram.venga.repos.CategorieCadeauRepository;
import com.ram.venga.util.NotFoundException;


@Service
public class CategorieCadeauService {

	private final CategorieCadeauMapper categorieCadeauMapper;
    private final CategorieCadeauRepository categorieCadeauRepository;
    private final CadeauxBARepository cadeauxBARepository;

    public CategorieCadeauService(final CategorieCadeauMapper categorieCadeauMapper,
                                  final CategorieCadeauRepository categorieCadeauRepository, CadeauxBARepository cadeauxBARepository) {
    	this.categorieCadeauMapper = categorieCadeauMapper;
        this.categorieCadeauRepository = categorieCadeauRepository;
        this.cadeauxBARepository = cadeauxBARepository;
    }

    public Page<CategorieCadeauDTO> findAll(String keyword,Pageable pageable) {
        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("dateCreated").descending()
        );
        final Page<CategorieCadeau> bonsAchat = categorieCadeauRepository.findAllByKeyword(keyword,pageable);
        return bonsAchat.map(categorieCadeauMapper::toDto);
    }

    public List<CategorieCadeauDTO> listAll() {
        final List<CategorieCadeau> bonsAchat = categorieCadeauRepository.findAll(Sort.by("dateCreated").descending()).stream().distinct().collect(Collectors.toList());
        return bonsAchat.stream().map(categorieCadeauMapper::toDto).toList();
    }

    public CategorieCadeauDTO get(final Long id) {
        return categorieCadeauRepository.findById(id)
                .map(categorieCadeau -> categorieCadeauMapper.toDto(categorieCadeau))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CategorieCadeauDTO categorieCadeauDTO) {
        try {
            return categorieCadeauRepository.save(categorieCadeauMapper.toEntity(categorieCadeauDTO)).getId();
        }catch (Exception e){
            String message = e.getCause().getCause().getMessage();
            if (message.contains("unique_categorie_cadeau_libelle")){
                throw new IllegalArgumentException("Cette catégorie existe déjà");
            }
            throw new IllegalArgumentException("Une erreur est survenue lors de la création de la catégorie");
        }
    }

    public void update(final Long id, final CategorieCadeauDTO categorieCadeauDTO) {
        final CategorieCadeau categorieCadeau = categorieCadeauRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        categorieCadeau.setLibelle(categorieCadeauDTO.getLibelle());
        //categorieCadeau.setCode(categorieCadeauDTO.getCode());
        categorieCadeauRepository.save(categorieCadeau);
    }

    public ResponseEntity<?> delete(final Long id) {
        List<CadeauxBA> cadeau = cadeauxBARepository.findByCategorieCadeauId(id);
        CategorieCadeau categorieCadeau =categorieCadeauRepository.findById(id).get();
        Map<String,String> map = new HashMap<>();
        String message = null;
        if (!cadeau.isEmpty()){
            map.put("message","Ce bon d'achat ne peut pas être supprimé car il contient au moins une commande.");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }

        categorieCadeauRepository.deleteById(id);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    public boolean codeExists(final String code) {
        return categorieCadeauRepository.existsByCodeIgnoreCase(code);
    }

}
