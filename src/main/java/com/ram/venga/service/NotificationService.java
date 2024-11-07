package com.ram.venga.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ram.venga.domain.Notification;
import com.ram.venga.mapper.NotificationMapper;
import com.ram.venga.model.NotificationDTO;
import com.ram.venga.repos.NotificationRepository;
import com.ram.venga.repos.UtilisateurRepository;
import com.ram.venga.util.NotFoundException;


@Service
public class NotificationService {

	private final NotificationMapper notificationMapper;
	private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public NotificationService(final NotificationMapper notificationMapper,
    		final NotificationRepository notificationRepository,
            final UtilisateurRepository utilisateurRepository) {
    	this.notificationMapper = notificationMapper;
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<NotificationDTO> findAll() {
        final List<Notification> notifications = notificationRepository.findAll(Sort.by("id"));
        return notifications.stream()
        		.map(notification -> notificationMapper.toDto(notification))
                .toList();
    }

    public NotificationDTO get(final Long id) {
        return notificationRepository.findById(id)
                .map(notification -> notificationMapper.toDto(notification))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final NotificationDTO notificationDTO) {
        final Notification notification = new Notification();
        notificationMapper.toEntity(notificationDTO);
        return notificationRepository.save(notification).getId();
    }

    public void update(final Long id, final NotificationDTO notificationDTO) {
        final Notification notification = notificationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        notificationMapper.toEntity(notificationDTO);
        notificationRepository.save(notification);
    }

    public void delete(final Long id) {
        notificationRepository.deleteById(id);
    }

}
