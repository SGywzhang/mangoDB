package client.cs4224c.transaction.relatedcustomer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.util.Collection;
import client.cs4224c.util.CollectionPool;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import client.cs4224c.transaction.relatedcustomer.data.RelatedCustomerData;
import java.util.regex.Pattern;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import java.util.Arrays;

public class RelatedCustomerTransaction extends AbstractTransaction 
{

    private final Logger logger = LoggerFactory.getLogger(RelatedCustomerTransaction.class);

    private RelatedCustomerData data;

    public RelatedCustomerData getData() 
    {
        return data;
    }

    public void setData(RelatedCustomerData data) 
    {
        this.data = data;
    }

    @Override
    public void executeFlow() 
    {
    	//first get the OrderSet
    	CollectionPool collectionPool = CollectionPool.getInstance();
    	int x = data.getC_W_ID();
    	int y = data.getC_ID();
        String customerKey = getCompoundKey(getStr(data.getC_W_ID()), getStr(data.getC_D_ID()), getStr(data.getC_ID()));

        String PatternStr = "^" + getStr(data.getC_W_ID()) + "-" + getStr(data.getC_D_ID()) + "-.*$";
		Pattern pattern = Pattern.compile(PatternStr, Pattern.CASE_INSENSITIVE);

        FindIterable<Document> OrderSet = collectionPool.getCollection(Collection.OrderItem)
        		.find ( Filters.and(
                    Filters.eq("o_c_id", data.getC_ID()), 
                    Filters.eq("_id", pattern) ));


        List <Set<Integer>> sortItem = new ArrayList<>();
        Set<Integer> tot = new HashSet<>();
        for (Document test:OrderSet)
        {
        	String now = test.getString("_id");
        	int FirstPos = now.indexOf ('-');
        	int SecondPost = now.indexOf ('-', FirstPos + 1);

        	Integer OrderId = Integer.valueOf (now.substring (SecondPost + 1));
            // System.out.println(String.format("   update! %d %d   %d", SecondPost, OrderId, test.getInteger("o_c_id")));
            List<Document> line = new ArrayList<Document>();
            line = (List<Document>)test.get("orderlines");
            Set<Integer>tmp = new HashSet<>();
            for (Document EachLine: line)
            {
            	// System.out.println(String.format("     %d  %d", EachLine.getInteger("ol_i_id"), EachLine.getInteger("ol_number")));
            	Integer item1 = EachLine.getInteger ("ol_i_id");
            	tmp.add (item1);
                tot.add (item1);
            }
            sortItem.add (tmp);

        }

        //find all possible customer
        Set<List<Integer>> PossibleAns = new HashSet <>();
        for (Integer each : tot)
        {
	        FindIterable<Document> possibleCustomer = collectionPool.getCollection(Collection.OrderItem)
	        		.find ( Filters.eq("orderlines.ol_i_id", each));
	        for (Document Each : possibleCustomer)
	        {

	        	String now = Each.getString("_id");
	        	int FirstPos = now.indexOf ('-');
	        	int SecondPost = now.indexOf ('-', FirstPos + 1);
            	// System.out.println(String.format("     %d ", Each.getInteger ("o_c_id")));
            	// System.out.println(String.format (now));
            	// System.out.println(String.format("    %d   %d    ", FirstPos, SecondPost));
            	List<Integer> cur = new ArrayList<>();
            	cur.add (Integer.valueOf (now.substring (0, FirstPos)));
            	cur.add (Integer.valueOf (now.substring (FirstPos + 1, SecondPost)));
            	// cur.add (Integer.valueOf (now.substring (SecondPost + 1)));
            	cur.add (Each.getInteger ("o_c_id"));
            	if (cur.get (0) == data.getC_W_ID ())
            		continue;
            	PossibleAns.add (cur);

            	//for multiple customer
	        	// List<Document> line = new ArrayList<Document>();
	         //    line = (List<Document>)order.get("o_c_id");
	         //    for (Document EachCus: line)
	         //    {
	         //    	List<Integer> cur = new ArrayList<>();
	         //    	cur.add (Integer.valueOf (now.substring (0, FirstPos)));
	         //    	cur.add (Integer.valueOf (now.substring (FirstPos + 1, SecondPost)));
          //   		cur.add (EachCus.getInteger ("o_c_id"));
	         //    	PossibleAns.add (cur);
	         //    }
	        }
        }

        List <List<Integer>> FinalAns = new ArrayList<>();
        Integer LenSortItem = sortItem.size ();
        //check each possible customer
        for (List<Integer> customer : PossibleAns)
        {
            // System.out.println(String.format("for each customer : %d  %d  %d", customer.get (0), customer.get (1), customer.get (2)));

        	int HaveAns = 0;
        	//find his order set
        	String ThisPatternStr = "^" + getStr(customer.get (0)) + "-" + getStr(customer.get (1)) + "-.*$";
			Pattern this_pattern = Pattern.compile(ThisPatternStr, Pattern.CASE_INSENSITIVE);

	        FindIterable<Document> OtherOrderSet = collectionPool.getCollection(Collection.OrderItem)
	        		.find ( Filters.and(
	                    Filters.eq("o_c_id", customer.get (2)),
	                    Filters.eq("_id", this_pattern) ));

	        //for his each order
	        for (Document order : OtherOrderSet)
	        {
            	// System.out.println(" new order :: ");
	        	if (HaveAns == 1)
	        		break;
	        	int[] record = new int[LenSortItem];
            	Arrays.fill(record, -1);

	        	List<Document> line = new ArrayList<Document>();
	            line = (List<Document>)order.get("orderlines");
	            for (Document EachLine: line)
	            {

	            	Integer item1 = EachLine.getInteger ("ol_i_id");
	            	
            		// System.out.println(String.format ("       item %d", item1));

	            	for (int i = 0; i < LenSortItem; i++)
	            		if (sortItem.get (i).contains (item1))
	            		{
	            			if (record[i] != -1 && record[i] != item1)
	            			{
	            				//is a ans !
            					// System.out.println(String.format ("          same !!!!!!!!1", ));
	            				FinalAns.add (customer);
	            				HaveAns = 1;
	            				break;
	            			}
	            			record[i] = item1;
            				// System.out.println(String.format ("          same with order [ %d ]", i));
	            		}
	            }
	        }
        }

        //print result
        System.out.println("The result of related customer : ");
        for (List<Integer> EachAns : FinalAns)
            	System.out.println(String.format("%d  %d  %d", EachAns.get (0), EachAns.get (1), EachAns.get (2)));

   	}
}