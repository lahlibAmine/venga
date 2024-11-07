package com.ram.venga.mapper;

import org.mapstruct.Mapper;

import com.ram.venga.domain.Segment;
import com.ram.venga.model.SegmentDTO;


@Mapper(componentModel = "spring")
public interface SegmentMapper extends EntityMapper<SegmentDTO, Segment> {
	
}