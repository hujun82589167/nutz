package org.nutz.dao.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.SqlManager;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.impl.entity.AnnotationEntityMaker;
import org.nutz.dao.impl.sql.NutPojoMaker;
import org.nutz.dao.impl.sql.run.NutDaoExecutor;
import org.nutz.dao.impl.sql.run.NutDaoRunner;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.PojoMaker;
import org.nutz.dao.sql.Sql;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * Dao 接口实现类的一些基础环境
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class DaoSupport {

	private static final Log log = Logs.get();

	/**
	 * 给子类使用的 Dao 的运行器，用来封装事务
	 */
	protected DaoRunner runner;

	/**
	 * 给子类使用的 Dao 语句执行器，用来具体运行某一条语句
	 */
	protected DaoExecutor executor;

	/**
	 * 给子类使用数据源
	 */
	protected DataSource dataSource;

	/**
	 * 给子类使用的数据特殊性的封装
	 */
	protected JdbcExpert expert;

	/**
	 * 给子类使用的 PojoStatementMaker 接口
	 */
	protected PojoMaker pojoMaker;

	/**
	 * 给子类使用的 Entity 获取对象
	 */
	protected EntityHolder holder;

	/**
	 * 数据库的描述
	 */
	private DatabaseMeta meta;

	/**
	 * SQL 管理接口实现类
	 */
	private SqlManager sqlManager;

	public DaoSupport() {
		this.runner = new NutDaoRunner();
		this.executor = new NutDaoExecutor();
	}

	/**
	 * @return Sql 管理接口的实例
	 */
	public SqlManager sqls() {
		return sqlManager;
	}

	/**
	 * @return 当前连接的数据库的一些描述数据
	 */
	public DatabaseMeta meta() {
		return meta;
	}

	/**
	 * 设置一个新的 Sql 管理接口实例
	 * 
	 * @param sqls
	 *            Sql 管理接口实例
	 */
	public void setSqlManager(SqlManager sqls) {
		this.sqlManager = sqls;
	}

	/**
	 * 设置一个新的 Dao 运行器
	 * 
	 * @param runner
	 *            运行器对象
	 */
	public void setRunner(DaoRunner runner) {
		this.runner = runner;
	}

	/**
	 * 设置一个新的 Dao 语句执行器
	 * 
	 * @param executor
	 *            Dao 语句执行器对象
	 */
	public void setExecutor(DaoExecutor executor) {
		this.executor = executor;
	}

	/**
	 * 设置一个新的 Pojo 语句创建器
	 * 
	 * @param pojoMaker
	 *            Pojo 语句创建器
	 */
	public void setPojoMaker(PojoMaker pojoMaker) {
		this.pojoMaker = pojoMaker;
	}

	/**
	 * @return 当前的 JDBC 专家类
	 */
	public JdbcExpert getJdbcExpert() {
		return expert;
	}

	/**
	 * 设置新的数据源。
	 * <p>
	 * 如果有老的数据源需要你在外部手动关闭
	 * 
	 * @param ds
	 *            数据源
	 */
	public void setDataSource(DataSource ds) {
		if (null != dataSource)
			if (log.isWarnEnabled())
				log.warn("Replaced a running dataSource!");
		dataSource = ds;
		expert = Jdbcs.getExpert(ds);
		pojoMaker = new NutPojoMaker(expert);

		meta = new DatabaseMeta();
		runner.run(dataSource, new ConnCallback() {
			public void invoke(Connection conn) throws Exception {
				DatabaseMetaData dmd = conn.getMetaData();
				meta.setProductName(dmd.getDatabaseProductName());
				meta.setVersion(dmd.getDatabaseProductVersion());
			}
		});

		holder = new EntityHolder(this);
		holder.maker = createEntityMaker();
	}

	public void execute(final Sql... sqls) {
		_exec(sqls);
	}

	public void run(ConnCallback callback) {
		runner.run(dataSource, callback);
	}

	protected int _exec(final DaoStatement... sts) {
		final int[] re = new int[1];
		runner.run(dataSource, new ConnCallback() {
			public void invoke(Connection conn) throws Exception {
				for (DaoStatement st : sts) {
					executor.exec(conn, st);
					re[0] += st.getUpdateCount();
				}
			}
		});
		return re[0];
	}

	/**
	 * 子类可以重写这个类，用来扩展成其他的实体配置方式
	 * 
	 * @return 实体工厂
	 */
	protected EntityMaker createEntityMaker() {
		return new AnnotationEntityMaker(dataSource, expert, holder);
	}

}
