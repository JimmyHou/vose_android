package com.vose.core.data.service.company;

import com.parse.ParseException;
import com.vose.core.data.dao.company.IndustryDAO;
import com.vose.data.model.company.ParseIndustry;

import java.util.List;

/**
 * Created by jimmyhou on 2014/11/27.
 */
public class IndustryServiceImpl implements  IndustryService {

    private IndustryDAO industryDAO;

    public IndustryServiceImpl(IndustryDAO industryDAO){
        this.industryDAO = industryDAO;
    }

    @Override
    public List<ParseIndustry> getIndustries() throws ParseException {
        return industryDAO.getIndustries();
    }
}
