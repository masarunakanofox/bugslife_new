package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import com.example.enums.CampaignStatus;
import com.example.enums.DiscountType;
import com.example.model.Campaign;
import com.example.repository.CampaignRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CampaignService {

	@Autowired
	private CampaignRepository campaignRepository;

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public CampaignService(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Campaign> findAll() {
		return campaignRepository.findAll();
	}

	public Optional<Campaign> findOne(Long id) {
		return campaignRepository.findById(id);
	}

	public Optional<Campaign> findByCode(String code) {
		return campaignRepository.findByCode(code);
	}

	@Transactional(readOnly = false)
	public Campaign save(Campaign entity) {
		return campaignRepository.save(entity);
	}

	@Transactional(readOnly = false)
	public void delete(Campaign entity) {
		campaignRepository.delete(entity);
	}

	/**
	 * CSVインポート処理
	 *
	 * @param file
	 * @throws IOException
	 */
	@Transactional
	public void importCSV(MultipartFile file) throws IOException {
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
			String line = br.readLine(); // 1行目はヘッダーなので読み飛ばす
			List<Campaign> campaignsToSave = new ArrayList<>();
			while ((line = br.readLine()) != null) {
				final String[] split = line.replace("\"", "").split(",");
				final Campaign campaign = new Campaign(
						split[0], split[1], split[2], split[3],
						DiscountType.valueOf(Integer.parseInt(split[4])),
						CampaignStatus.valueOf(Integer.parseInt(split[5])), split[6]);
				campaignsToSave.add(campaign);
			}
			batchInsert(campaignsToSave); // 一括更新処理を呼び出す
		} catch (IOException e) {
			throw new RuntimeException("ファイルが読み込めません", e);
		}
	}

	/**
	 * 一括更新処理実行
	 *
	 * @param campaigns
	 */
	@SuppressWarnings("unused")
	private int[] batchInsert(List<Campaign> campaigns) {
		String sql = "INSERT INTO campaigns (name, code, from_date, to_date, discount_type, status, description, create_at, update_at)"
				+ " VALUES(:name, :code, :from_date, :to_date, :discount_type, :status, :description, :create_at, :update_at)";
		return jdbcTemplate.batchUpdate(sql,
				campaigns.stream()
						.map(c -> new MapSqlParameterSource()
								.addValue("name", c.getName())
								.addValue("code", c.getCode())
								.addValue("from_date", c.getFromDate())
								.addValue("to_date", c.getToDate())
								.addValue("discount_type", c.getDiscountType().getId())
								.addValue("status", c.getStatus().getId())
								.addValue("description", c.getDescription())
								.addValue("create_at", new Date())
								.addValue("update_at", new Date()))
						.toArray(SqlParameterSource[]::new));
	}

	/**
	 * 一括ステータス更新処理 更新前にステータスをチェックする
	 *
	 * @param idList    更新対象IDリスト
	 * @param nexStatus 更新後ステータス
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = RuntimeException.class)
	public void bulkStatusUpdate(List<Long> idList, CampaignStatus nexStatus) throws Exception {
		idList.forEach(id -> {
			try {
				Campaign campaign = campaignRepository.findById(id).get();
				if (nexStatus.getId() == campaign.getStatus().getId()) {
					throw new RuntimeException(campaign.getName() + "にステータスの変更がないため、ステータスの一括更新に失敗しました。");
				}
				campaign.setStatus(nexStatus);
				campaignRepository.save(campaign);
			} catch (RuntimeException e) {
				throw new RuntimeException(e.getMessage());
			}
		});
	}

}