package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startinpoint.proj.familyapp.util.CalendarUtil;
import com.startinpoint.proj.familyapp.webservice.dao.CheckListActivityDao;
import com.startinpoint.proj.familyapp.webservice.entity.CheckListActivity;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Activity;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.entity.pojo.ActivityLog;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Repository("checkListActivityDao")
public class CheckListActivityDaoImpl implements CheckListActivityDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
		} catch (Exception e) {
			session = sessionFactory.openSession();
		}
		return session;
	}
	
	@Override
	public CheckListActivity saveOrUpdateCheckListActivity(CheckListActivity activity)
			throws FamilyAppWebserviceException {
		getSession().saveOrUpdate(activity);
		return activity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivityLog> getCheckListActivityLog(Long checkListId, Activity activity, int size)
			throws FamilyAppWebserviceException {
		String sql = "SELECT * FROM ("
				+ " (SELECT comment.check_list_id, comment.created_date,comment.updated_date, comment.comment,up.username ,up.profile_image_url,NULL as task_name ,NULL as parent_task_name,NULL as action  ,'COMMENT' AS type"
				+ " FROM check_list_comment comment" + " JOIN user_profile up ON up.id = comment.member_id)"
				+ " UNION ALL"
				+ " (SELECT activity.check_list_id,activity.created_date,NULL as updated_date,NULL as comment,activity.username ,NULL as profile_image_url , activity.task_name ,activity.parent_task_name ,activity.action, 'ACTIVITY' AS type"
				+ " FROM check_list_activity activity)" + " ) results" + " WHERE results.check_list_id = "+checkListId
				+ " ORDER BY created_date DESC" + " LIMIT "+size;
		
		SQLQuery query = getSession().createSQLQuery(sql);
		List<Object[]> rows = query.list();
		List<ActivityLog> list = new ArrayList<>();
		for(Object[] row : rows){
			ActivityLog log = new ActivityLog();
			log.setCheckListId(Long.parseLong(row[0].toString()));
			log.setCreatedDate((Date)row[1]);
			if(row[2] != null){
				log.setUpdatedDate((Date)row[2]);
			}
			if(row[3] != null){
				log.setComment(row[3].toString());
			}
			if(row[4] != null){
				log.setUsername(row[4].toString()); //image url
			}
			if(row[5] != null){
				log.setImageUrl(row[5].toString()); //image url
			}
			if(row[6] != null){
				log.setTaskName(row[6].toString()); //task name
			}
			if(row[7] != null){
				log.setParentTaskName(row[7].toString()); //parent task name
			}
			if(row[8] != null){
				log.setAction(row[8].toString());
			}
			
			log.setType(Activity.valueOf(row[9].toString()));
			list.add(log);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivityLog> getCheckListActivityLogByScrollStatus(Long checkListId, Activity activity, int size,
			ScrollStatus scrollStatus, Date lastDate) throws FamilyAppWebserviceException {
		String sql = "SELECT * FROM ("
				+ " (SELECT comment.check_list_id, comment.created_date,comment.updated_date, comment.comment,up.username ,up.profile_image_url,NULL as task_name ,NULL as parent_task_name,NULL as action  ,'COMMENT' AS type"
				+ " FROM check_list_comment comment" + " JOIN user_profile up ON up.id = comment.member_id)"
				+ " UNION ALL"
				+ " (SELECT activity.check_list_id,activity.created_date,NULL as updated_date,NULL as comment,activity.username ,NULL as profile_image_url , activity.task_name ,activity.parent_task_name ,activity.action, 'ACTIVITY' AS type"
				+ " FROM check_list_activity activity)" + " ) results" + " WHERE results.check_list_id = "+checkListId;
		
		
		//noted:: created_date ordering by descending
		if(scrollStatus == ScrollStatus.BEFORE){
			
			sql += " AND created_date > '"+CalendarUtil.formatDate(lastDate, FamilyAppConst.DB_CALENDAR_DATETIME_FORMAT)+"'";
		}
		else if(scrollStatus == ScrollStatus.AFTER){
			sql += " AND created_date < '"+CalendarUtil.formatDate(lastDate, FamilyAppConst.DB_CALENDAR_DATETIME_FORMAT)+"'";
		}
		
		sql += " ORDER BY created_date DESC" + " LIMIT "+size;
		
		SQLQuery query = getSession().createSQLQuery(sql);
		List<Object[]> rows = query.list();
		List<ActivityLog> list = new ArrayList<>();
		for(Object[] row : rows){
			ActivityLog log = new ActivityLog();
			log.setCheckListId(Long.parseLong(row[0].toString()));
			log.setCreatedDate((Date)row[1]);
			if(row[2] != null){
				log.setUpdatedDate((Date)row[2]);
			}
			if(row[3] != null){
				log.setComment(row[3].toString());
			}
			if(row[4] != null){
				log.setUsername(row[4].toString()); //image url
			}
			if(row[5] != null){
				log.setImageUrl(row[5].toString()); //image url
			}
			if(row[6] != null){
				log.setTaskName(row[6].toString()); //task name
			}
			if(row[7] != null){
				log.setParentTaskName(row[7].toString()); //parent task name
			}
			if(row[8] != null){
				log.setAction(row[8].toString());
			}
			
			log.setType(Activity.valueOf(row[9].toString()));
			list.add(log);
		}
		return list;
	}
	
	
}
