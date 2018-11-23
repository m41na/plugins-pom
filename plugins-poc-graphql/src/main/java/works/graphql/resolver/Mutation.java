package works.graphql.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;

import works.graphql.dao.LinkRepository;
import works.graphql.entity.Address;
import works.graphql.entity.Blog;
import works.graphql.entity.Profile;
import works.graphql.model.Link;
import works.graphql.service.AccountService;
import works.graphql.service.PublicationService;

@Component
public class Mutation implements GraphQLMutationResolver{

	private final LinkRepository linkRepo;
	private final AccountService accountService;
	private PublicationService publicationService;

	public Mutation(@Autowired LinkRepository linkRepo, @Autowired AccountService accountService, @Autowired PublicationService publicationService) {
		super();
		this.linkRepo = linkRepo;
		this.accountService = accountService;
		this.publicationService = publicationService;
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
	
	public Integer updateAddress(Long id, String street, String unit, String city, String state, String zipCode) {
		Address addr = new Address();
		addr.street = street;
		addr.unit = unit;
		addr.city = city;
		addr.state = state;
		addr.zipCode = zipCode;
		return accountService.updateAddress(id, addr);
	}
	
	public Integer createBlog(Long authorId, String title, String preface, String content) {
		Profile author = new Profile();
		author.id = authorId;
		Blog blog = new Blog();
		blog.author = author;
		blog.title = title;
		blog.preface = preface;
		blog.content = content;
		return publicationService.createBlog(blog);
	}
}
