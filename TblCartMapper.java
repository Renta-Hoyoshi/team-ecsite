package jp.co.internous.cony.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import jp.co.internous.cony.model.domain.TblCart;
import jp.co.internous.cony.model.domain.dto.CartDto;

@Mapper
public interface TblCartMapper {
	//カート情報を取ってくるとき
	List<CartDto> findByUserId(@Param("userId") int userId);
	
	//ログインしたとき
	@Update("UPDATE tbl_cart SET user_id = #{userId}, updated_at = now() WHERE user_id = #{tempUserId}")
	int updateUserId(@Param("userId") int userId, @Param("tempUserId") int tempUserId);
	
	//商品の有無チェック
	@Select("SELECT count(product_count) FROM tbl_cart WHERE user_id = #{userId} AND product_id = #{productId}")
	int findByUserIdAndProductId(@Param("userId") int userId, @Param("productId") int productId);
	
	//追加した商品がカートにないとき
	@Insert("INSERT INTO tbl_cart (user_id, product_id, product_count) " +
			"VALUES (#{userId}, #{productId}, #{productCount})")
	@Options(useGeneratedKeys=true, keyProperty="id")
	void insert(TblCart tblCart);
	
	//カートにすでに追加した商品があるとき
	@Update("UPDATE tbl_cart SET product_count = product_count + #{productCount}, updated_at = now()"
    		+ " WHERE user_id = #{userId} "
    		+ "AND product_id = #{productId}")
	int update(@Param("productId") int productId, @Param("userId") int userId,
				@Param("productCount") int productCount);
	
	//チェックしたカート商品削除
	@Delete("DELETE FROM tbl_cart WHERE id = #{id} ")
	int deleteByCartId(@Param("id") int id);
	
	//決済されたとき
	@Delete("DELETE FROM tbl_cart WHERE user_id = #{userId}")
	int deleteByUserId(int userId);
	
}
