package works.graphql.resolver;

import java.util.List;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

import works.graphql.dao.LinkRepository;
import works.graphql.entity.Account;
import works.graphql.model.Link;
import works.graphql.service.AccountService;

public class Query implements GraphQLRootResolver {
    
    private final LinkRepository linkRepo;
    private final AccountService accountService;
    
    public Query(LinkRepository linkRepo, AccountService accountService) {
    	super();
        this.linkRepo = linkRepo;
        this.accountService = accountService;
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
