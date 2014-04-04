package com.zoe.crawler.implement;

import java.util.HashSet;
import java.util.Set;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
/**
 * this class is used to extract links from a specific url
 * and also definite the url-filtering rules
 * @author zhanglm
 *
 */
public class HttpParserTool {
	/**
	 * declaration of LinkFilter interface
	 * @author zhanglm
	 *
	 */
	public interface LinkFilter {
		public boolean accept(String url);
	}
	/**
	 * extract links from the url that passed in
	 * @param url
	 * @param filter the filter rules
	 * @return the set of urls 
	 */
	public static Set<String> extracLinks(String url,LinkFilter filter) {
		Set<String> links = new HashSet<String>();
		try {
			Parser parser = new Parser(url);
			parser.setEncoding("gb2312");
			NodeFilter frameFilter = new NodeFilter() {
				/**
				 * add serialVersionUID
				 */
				private static final long serialVersionUID = 1L;

				public boolean accept(Node node) {
					if(node.getText().startsWith("frame src=")) {
						return true;
					} else {
						return false;
					}
				}
			};
				OrFilter linkFilter = new OrFilter(new NodeClassFilter(LinkTag.class), frameFilter);
				NodeList list = parser.extractAllNodesThatMatch(linkFilter);
				for(int i=0;i<list.size();i++) {
					Node tag = list.elementAt(i);
					if(tag instanceof LinkTag) {//<a>
						LinkTag link = (LinkTag) tag;
						String linkUrl = link.getLink();//URL
						if(filter.accept(linkUrl))
							links.add(linkUrl);
					}else {//<frame>
						//��<frame src="test.html"/>
						String frame = tag.getText();
						int start = frame.indexOf("src=");
						frame = frame.substring(start);
						int end = frame.indexOf(" ");
						if (end == -1)
							end = frame.indexOf(">");
						try{
						String frameUrl = frame.substring(5, end-1);
						if (filter.accept(frameUrl))
							links.add(frameUrl);
						} catch (StringIndexOutOfBoundsException e){
							continue;
						}
					}
				}
		}catch(ParserException e) {
			System.out.println("ParserException: "+url);
//			e.printStackTrace();
		} 
		return links;
	}
}
