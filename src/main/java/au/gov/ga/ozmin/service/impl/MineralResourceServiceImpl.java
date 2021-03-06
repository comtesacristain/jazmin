package au.gov.ga.ozmin.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.gov.ga.ozmin.model.MineralDeposit;
import au.gov.ga.ozmin.model.MineralResource;
import au.gov.ga.ozmin.repository.MineralResourceRepository;
import au.gov.ga.ozmin.service.MineralResourceService;
import au.gov.ga.ozmin.specification.MineralResourceSpecification;

@Service
public class MineralResourceServiceImpl implements MineralResourceService {

	@Inject
	private MineralResourceRepository mineralResourceRepository;

	@Override
	public Page<MineralResource> listMineralResources(Pageable pageRequest) {

		return this.mineralResourceRepository.findAll(pageRequest);

	}

	@Override
	public Set<MineralDeposit> mineralResourcesCollectionForQualityCheck(String qaStatus, String enteredBy) {
		return this.mineralResourceRepository.findResourcesCollectionForQaByDeposit(qaStatus,enteredBy);
	}

	@Override
	@Transactional
	public MineralResource getMineralResourceById(Long id) {
		MineralResource mineralResource = this.mineralResourceRepository.findOne(id);

		return mineralResource;
	}

	@Override
	public Page<MineralResource> listMineralResourcesForAdministration(Pageable pageable, String enteredBy,
			Date startDate, Date endDate) {

		if (enteredBy == null) {
			return this.mineralResourceRepository.findByQaStatusAndEntryDateBetween(pageable, "U", startDate, endDate);
		} else {
			return this.mineralResourceRepository.findByQaStatusAndEnteredByAndEntryDateBetween(pageable, "U",
					enteredBy, startDate, endDate);
		}
	}

	@Override
	public Page<MineralResource> mineralResources(Specification<MineralResource> specification, Pageable pageable) {
		return this.mineralResourceRepository.findAll(specification, pageable);
	}
}
