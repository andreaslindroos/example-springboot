package eu.lindroos.taas.teams.service.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Andreas on 23.4.2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrowsePage<T> {
    private int size;
    private int page;
    private long totalItems;
    private int totalPages;
    private List<T> items;
}
