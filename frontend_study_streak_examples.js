// Study Streak Frontend Integration Examples
// Ready-to-use JavaScript/React code for Study Streak system

// ============================================================================
// 1. Study Streak Service Class
// ============================================================================

class StudyStreakService {
  constructor(apiClient) {
    this.api = apiClient;
  }

  // Complete daily task
  async completeDailyTask(userId, taskType, contentId, metadata = null) {
    try {
      const response = await this.api.post(`/api/users/${userId}/streak/complete-task`, {
        taskType,
        contentId,
        metadata
      });
      
      return response.data;
    } catch (error) {
      console.error('Failed to complete daily task:', error);
      throw error;
    }
  }

  // Get streak information
  async getStreak(userId) {
    try {
      const response = await this.api.get(`/api/users/${userId}/streak`);
      return response.data;
    } catch (error) {
      console.error('Failed to get streak:', error);
      throw error;
    }
  }

  // Get detailed status
  async getStreakStatus(userId) {
    try {
      const response = await this.api.get(`/api/users/${userId}/streak/status`);
      return response.data;
    } catch (error) {
      console.error('Failed to get streak status:', error);
      throw error;
    }
  }

  // Process daily check (testing)
  async processDailyCheck(userId) {
    try {
      const response = await this.api.post(`/api/users/${userId}/streak/daily-check`);
      return response.data;
    } catch (error) {
      console.error('Failed to process daily check:', error);
      throw error;
    }
  }
}

// ============================================================================
// 2. React Hook for Study Streak
// ============================================================================

import { useState, useEffect, useCallback } from 'react';

export const useStudyStreak = (userId) => {
  const [streakData, setStreakData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const streakService = new StudyStreakService(apiClient);

  const loadStreakData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await streakService.getStreakStatus(userId);
      setStreakData(data);
    } catch (err) {
      setError(err.message);
      console.error('Failed to load streak data:', err);
    } finally {
      setLoading(false);
    }
  }, [userId, streakService]);

  const completeTask = useCallback(async (taskType, contentId, metadata = null) => {
    try {
      setError(null);
      const result = await streakService.completeDailyTask(userId, taskType, contentId, metadata);
      
      // Update local state
      setStreakData(prev => ({
        ...prev,
        streakCount: result.streakCount,
        freezingCount: result.freezingCount,
        hasCompletedToday: true,
        lastActivityTime: result.lastActivityTime
      }));
      
      return result;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  }, [userId, streakService]);

  const processDailyCheck = useCallback(async () => {
    try {
      setError(null);
      const result = await streakService.processDailyCheck(userId);
      
      // Update local state
      setStreakData(prev => ({
        ...prev,
        streakCount: result.streakCount,
        freezingCount: result.freezingCount,
        lastActivityTime: result.lastActivityTime
      }));
      
      return result;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  }, [userId, streakService]);

  useEffect(() => {
    if (userId) {
      loadStreakData();
    }
  }, [userId, loadStreakData]);

  return {
    streakData,
    loading,
    error,
    completeTask,
    processDailyCheck,
    refresh: loadStreakData
  };
};

// ============================================================================
// 3. React Component Examples
// ============================================================================

// Study Streak Widget Component
export const StudyStreakWidget = ({ userId }) => {
  const { streakData, loading, error, completeTask } = useStudyStreak(userId);

  if (loading) return <div className="streak-widget loading">Loading streak data...</div>;
  if (error) return <div className="streak-widget error">Error: {error}</div>;
  if (!streakData) return <div className="streak-widget empty">No streak data</div>;

  return (
    <div className="streak-widget">
      <div className="streak-header">
        <h3>🔥 Study Streak</h3>
        <div className="streak-count">{streakData.streakCount} days</div>
      </div>
      
      <div className="streak-status">
        {streakData.hasCompletedToday ? (
          <div className="completed-today">
            ✅ Completed today!
          </div>
        ) : (
          <div className="not-completed">
            ⚠️ Complete a task to maintain your streak
          </div>
        )}
      </div>
      
      {streakData.freezingCount > 0 && (
        <div className="freezing-warning">
          ⚠️ {streakData.freezingCount} missed days
        </div>
      )}
      
      <div className="status-message">
        {streakData.statusMessage}
      </div>
      
      {streakData.motivationalQuote && (
        <div className="motivational-quote">
          💡 {streakData.motivationalQuote}
        </div>
      )}
    </div>
  );
};

// Study Streak Progress Bar
export const StudyStreakProgress = ({ userId }) => {
  const { streakData, loading } = useStudyStreak(userId);

  if (loading || !streakData) return null;

  const progress = streakData.hasCompletedToday ? 100 : 0;
  const streakLevel = Math.floor(streakData.streakCount / 7); // Weekly levels

  return (
    <div className="streak-progress">
      <div className="progress-header">
        <span>Daily Progress</span>
        <span>{progress}%</span>
      </div>
      <div className="progress-bar">
        <div 
          className="progress-fill" 
          style={{ width: `${progress}%` }}
        />
      </div>
      <div className="streak-info">
        <span>🔥 {streakData.streakCount} day streak</span>
        <span>Level {streakLevel}</span>
      </div>
    </div>
  );
};

// ============================================================================
// 4. Integration Examples
// ============================================================================

// Content Creation Integration
export const onContentCreated = async (contentId, contentType, userId) => {
  try {
    // Save content first
    await saveContent(contentData);
    
    // Update streak
    const result = await streakService.completeDailyTask(
      userId, 
      'CREATED_CONTENT', 
      contentId,
      `Created ${contentType}`
    );
    
    // Show success notification
    showNotification('Content created and streak updated!', 'success');
    showStreakNotification(result);
    
  } catch (error) {
    console.error('Failed to create content and update streak:', error);
    showNotification('Failed to update streak', 'error');
  }
};

// Interactive Content Completion Integration
export const onInteractiveCompleted = async (contentId, contentType, userId) => {
  try {
    // Save interactive result
    await saveInteractiveResult(resultData);
    
    // Update streak
    const result = await streakService.completeDailyTask(
      userId, 
      'INTERACTIVE_MODE', 
      contentId,
      `Completed ${contentType}`
    );
    
    // Show success notification
    showNotification('Great job! Streak updated!', 'success');
    showStreakNotification(result);
    
  } catch (error) {
    console.error('Failed to complete interactive content and update streak:', error);
    showNotification('Failed to update streak', 'error');
  }
};

// ============================================================================
// 5. Notification Functions
// ============================================================================

export const showStreakNotification = (result) => {
  if (result.streakCount === 1) {
    showNotification('🎉 First streak! Keep it up!', 'success');
  } else if (result.streakCount % 7 === 0) {
    showNotification(`🔥 ${result.streakCount} day streak! Amazing!`, 'success');
  } else if (result.streakCount % 30 === 0) {
    showNotification(`🏆 ${result.streakCount} day streak! Incredible!`, 'success');
  } else {
    showNotification(`🔥 Streak updated to ${result.streakCount} days!`, 'success');
  }
};

export const showNotification = (message, type = 'info') => {
  // Implementation depends on your notification system
  console.log(`[${type.toUpperCase()}] ${message}`);
  
  // Example with toast notification
  if (window.toast) {
    window.toast(message, type);
  }
};

// ============================================================================
// 6. Utility Functions
// ============================================================================

export const formatStreakTime = (lastActivityTime) => {
  if (!lastActivityTime) return 'Never';
  
  const now = new Date();
  const lastTime = new Date(lastActivityTime);
  const diffMs = now - lastTime;
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
  
  if (diffDays === 0) return 'Today';
  if (diffDays === 1) return 'Yesterday';
  return `${diffDays} days ago`;
};

export const getStreakEmoji = (streakCount) => {
  if (streakCount === 0) return '💤';
  if (streakCount < 7) return '🔥';
  if (streakCount < 30) return '🚀';
  if (streakCount < 100) return '🏆';
  return '👑';
};

export const getStreakLevel = (streakCount) => {
  return Math.floor(streakCount / 7) + 1;
};

// ============================================================================
// 7. CSS Styles (Optional)
// ============================================================================

export const streakStyles = `
.streak-widget {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.streak-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.streak-count {
  font-size: 24px;
  font-weight: bold;
}

.completed-today {
  background: rgba(76, 175, 80, 0.2);
  padding: 8px 12px;
  border-radius: 6px;
  margin: 10px 0;
}

.not-completed {
  background: rgba(255, 152, 0, 0.2);
  padding: 8px 12px;
  border-radius: 6px;
  margin: 10px 0;
}

.freezing-warning {
  background: rgba(244, 67, 54, 0.2);
  padding: 8px 12px;
  border-radius: 6px;
  margin: 10px 0;
}

.motivational-quote {
  background: rgba(255, 255, 255, 0.1);
  padding: 12px;
  border-radius: 6px;
  margin-top: 15px;
  font-style: italic;
}

.streak-progress {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 8px;
  margin: 10px 0;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.progress-bar {
  background: #e0e0e0;
  height: 8px;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  background: linear-gradient(90deg, #4CAF50, #8BC34A);
  height: 100%;
  transition: width 0.3s ease;
}

.streak-info {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 14px;
  color: #666;
}
`;

// ============================================================================
// 8. Usage Examples
// ============================================================================

// Example 1: Basic usage in React component
/*
function MyComponent() {
  const { streakData, completeTask } = useStudyStreak(currentUserId);
  
  const handleCreateContent = async (contentId) => {
    await completeTask('CREATED_CONTENT', contentId);
  };
  
  return (
    <div>
      <StudyStreakWidget userId={currentUserId} />
      <button onClick={() => handleCreateContent(123)}>
        Create Content
      </button>
    </div>
  );
}
*/

// Example 2: Integration with existing content creation
/*
async function createQuiz(quizData) {
  // Create quiz
  const quiz = await quizService.createQuiz(quizData);
  
  // Update streak
  await onContentCreated(quiz.id, 'quiz', currentUserId);
  
  return quiz;
}
*/

// Example 3: Integration with interactive content
/*
async function completeFlashcard(flashcardId, results) {
  // Save results
  await flashcardService.saveResults(flashcardId, results);
  
  // Update streak
  await onInteractiveCompleted(flashcardId, 'flashcard', currentUserId);
}
*/

export default {
  StudyStreakService,
  useStudyStreak,
  StudyStreakWidget,
  StudyStreakProgress,
  onContentCreated,
  onInteractiveCompleted,
  showStreakNotification,
  formatStreakTime,
  getStreakEmoji,
  getStreakLevel,
  streakStyles
};
