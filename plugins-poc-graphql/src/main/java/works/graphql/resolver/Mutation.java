package works.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

import works.graphql.dao.LinkRepository;
import works.graphql.entity.Address;
import works.graphql.model.Link;
import works.graphql.service.AccountService;

public class Mutation implements GraphQLRootResolver {

	private final LinkRepository linkRepo;
	private final AccountService accountService;

	public Mutation(LinkRepository linkRepo, AccountService accountService) {
		super();
		this.linkRepo = linkRepo;
		this.accountService = accountService;
	}

	public Link createLink(String url, String description) {
		Link newLink = new Link(url, description);
		linkRepo.saveLink(newLink);
		return newLink;
	}

	public Integer createAccount(String username, String password, String emailAddr) {
		return accountService.createAccount(username, password, emailAddr);
	}
	
	public Integer updateProfile(String id, String firstName, String lastName, String aboutMe, String birthDay) {
		return accountService.updateProfile(Long.valueOf(id), firstName, lastName, aboutMe, birthDay);
	}
	
	public Integer updateAddress(Long id, Address address) {
		return accountService.updateAddress(id, address);
	}
}
