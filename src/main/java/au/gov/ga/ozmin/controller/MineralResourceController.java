package au.gov.ga.ozmin.controller;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import au.gov.ga.ozmin.model.MineralDeposit;
import au.gov.ga.ozmin.model.MineralResource;
import au.gov.ga.ozmin.service.MineralResourceService;
import au.gov.ga.ozmin.specification.MineralResourceSpecification;
import au.gov.ga.ozmin.util.Paginator;
import au.gov.ga.ozmin.view.ResourceQualityCheckPdfView;

@Controller
@RequestMapping("/mineralResources")
public class MineralResourceController {
	private MineralResourceService mineralResourceService;

	@Autowired(required = true)
	@Qualifier(value = "mineralResourceService")
	public void setMineralResourceService(MineralResourceService mrs) {
		this.mineralResourceService = mrs;
	}

	@Autowired
	@Qualifier(value = "resourceQualityCheckPdfView")
	private ResourceQualityCheckPdfView resourceQualityCheckPdfView;

	@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Page<MineralResource> mineralResources(@RequestParam(required = false, value = "qaStatus") String qaStatus,
			@RequestParam(value = "enteredBy", required = false) String enteredBy,
			@RequestParam(value = "entryDate", required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate entryDate,
			Pageable pageable) {

	    Page<MineralResource> mineralResources = this.mineralResourceService.mineralResources(MineralResourceSpecification.findMostRecentResource(entryDate), pageable);
		return mineralResources;
		//return this.mineralResourceService
				//.mineralResources(MineralResourceSpecification.searchByParameters(qaStatus, enteredBy), pageable);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getResourceById(@PathVariable("id") Long id, Model model) {
		MineralResource mineralResource = this.mineralResourceService.getMineralResourceById(id);
		model.addAttribute("mineralResource", mineralResource);
		return "mineralResources/show";
	}

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String resourcesAdmin(Model model, Pageable pageable,
			@RequestParam(value = "enteredBy", required = false) String enteredBy,
			@RequestParam(value = "entryDate", required = false) @DateTimeFormat(iso = ISO.DATE) Date entryDate,
			@RequestParam(value = "range", defaultValue = "week") Range range) {

		if (entryDate == null) {
			entryDate = new Date();
		}
		int calendarEnum;
		int calendarValue;
		switch (range) {
		case week:
			calendarEnum = Calendar.DAY_OF_YEAR;
			calendarValue = 7;
			break;
		case month:
			calendarEnum = Calendar.MONTH;
			calendarValue = 1;
			break;
		case year:
			calendarEnum = Calendar.YEAR;
			calendarValue = 1;
			break;
		case all:
			calendarEnum = Calendar.YEAR;
			calendarValue = 100;
			break;
		default:
			calendarEnum = Calendar.DAY_OF_YEAR;
			calendarValue = 7;
			break;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(entryDate);
		calendar.add(calendarEnum, -calendarValue);
		Date startDate = calendar.getTime();
		calendar.setTime(entryDate);
		calendar.add(calendarEnum, calendarValue);
		Date endDate = calendar.getTime();

		Paginator<MineralResource> resourcesPage = new Paginator<MineralResource>(
				mineralResourceService.listMineralResourcesForAdministration(pageable, enteredBy, startDate, endDate),
				"/mineralResources/admin");

		model.addAttribute("listMineralResources", resourcesPage);

		return "mineralResources/admin";
	}

	@RequestMapping(value = "/qa", method = RequestMethod.GET)
	public String resourcesQualityCheck(Model model, Pageable pageable) {
		return "mineralResources/qa";
	}

	@RequestMapping(value = "/qa.pdf", method = RequestMethod.GET)
	public ModelAndView resourcesQualityCheckPrintable(
			@RequestParam(value = "qaStatus", defaultValue = "U") String qaStatus,
			@RequestParam(value = "enteredBy", defaultValue = "MSEXTON1") String enteredBy) {
		Set<MineralDeposit> resourcesCollection = mineralResourceService
				.mineralResourcesCollectionForQualityCheck(qaStatus, enteredBy);

		return new ModelAndView(resourceQualityCheckPdfView, "resourcesCollection", resourcesCollection);
	}

	private enum Range {
		week, month, year, all
	}

}
