package works.graphql.resolver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import works.graphql.dao.LinkRepository;
import works.graphql.entity.Account;
import works.graphql.model.Link;
import works.graphql.service.AccountService;
import works.graphql.service.PublicationService;

@Component
public class Query implements GraphQLQueryResolver {
    
    private final LinkRepository linkRepo;
    private final AccountService accountService;
    private final PublicationService publicationService;
    
    public Query(@Autowired LinkRepository linkRepo, @Autowired AccountService accountService, @Autowired PublicationService publicationService) {
    	super();
        this.linkRepo = linkRepo;
        this.accountService = accountService;
        this.publicationService = publicationService;
    }
    
    public List<Link> allLinks() {
        return linkRepo.getAllLinks();
    }

    public List<Account> allAccounts() {
        return accountService.fetchAccounts();
    }
    
    public Account accountById(Long id) {
    	return accountService.findById(id);
    }
    
    public Account accountByUsername(String user) {
    	return accountService.findByUsername(user);
    }
}
