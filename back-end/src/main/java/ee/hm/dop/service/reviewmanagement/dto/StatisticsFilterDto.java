package ee.hm.dop.service.reviewmanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;

import java.util.List;

public class StatisticsFilterDto {

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime from;
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime to;
    private List<Taxon> taxons;
    private List<User> users;
    private FileFormat format;

    @JsonIgnore
    public boolean isValidSearch(){
        if (CollectionUtils.isNotEmpty(users) && CollectionUtils.isNotEmpty(taxons)){
            return false;
        }
        return CollectionUtils.isNotEmpty(users) || CollectionUtils.isNotEmpty(taxons);
    }

    @JsonIgnore
    public boolean isValidExportRequest(){
        return isValidSearch() && format != null;
    }

    @JsonIgnore
    public boolean isUserSearch(){
        return CollectionUtils.isNotEmpty(users);
    }

    public DateTime getFrom() {
        return from;
    }

    public void setFrom(DateTime from) {
        this.from = from;
    }

    public DateTime getTo() {
        return to;
    }

    public void setTo(DateTime to) {
        this.to = to;
    }

    public List<Taxon> getTaxons() {
        return taxons;
    }

    public void setTaxons(List<Taxon> taxons) {
        this.taxons = taxons;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public FileFormat getFormat() {
        return format;
    }

    public void setFormat(FileFormat format) {
        this.format = format;
    }
}
