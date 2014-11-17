package proxy;

/**
 * Enum que diferencia os dois tipos de listas de URLs, as BlackLists e Whitelists:
 * <ul>
 * <li> Blacklist: Somente as URLs da lista sao bloqueadas</li>
 * <li> Whitelist: Todas as URLs sao bloqueadas, exceto as que estao na lista </li>
 * </ul>
 * 
 * @author <img src="https://avatars2.githubusercontent.com/u/3778188?v=2&s=30" width="30" height="30" /> <a href="https://github.com/DRA2840" target="_blank"> DRA2840 </a>
 *
 */
public enum ListType{
	BLACK_LIST,
	WHITE_LIST;
}