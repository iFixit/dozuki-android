package com.dozuki.ifixit.model.guide;

import com.dozuki.ifixit.model.Comment;
import com.dozuki.ifixit.model.Document;
import com.dozuki.ifixit.model.Image;
import com.dozuki.ifixit.model.Item;

import java.io.Serializable;
import java.util.ArrayList;

public class Guide implements Serializable {
   private static final long serialVersionUID = -1965203088124961396L;
   private static final int NEW_GUIDE_ID = -1;

   protected int mGuideid;
   /**
    * Guide's revisionid or null if this is a new guide.
    */
   protected Integer mRevisionid;
   protected String mTitle;
   protected boolean mPublic;
   protected String mTopic;
   protected String mAuthor;
   protected String mType;
   protected String mTimeRequired;
   protected String mDifficulty;
   protected String mLocale = "en";
   protected String mIntroductionRendered;
   protected String mIntroductionRaw;
   protected String mSubject;
   protected Image mIntroImage = new Image();
   protected String mSummary;
   protected ArrayList<GuideStep> mSteps = new ArrayList<>();
   protected ArrayList<Item> mTools = new ArrayList<>();
   protected ArrayList<Item> mParts = new ArrayList<>();
   protected ArrayList<Document> mDocuments = new ArrayList<>();
   protected int mFeaturedDocumentId;
   protected boolean mCompleted;
   protected String mConclusion;
   protected boolean mCanEdit = true;
   protected int mPatrolThreshold = 0;
   protected boolean mFavorited = false;
   protected double mModifiedDate;
   protected double mPrereqModifiedDate;

   /**
    * Collection of general user comments on the guide
    */
   protected ArrayList<Comment> mComments;
   private int mFakeCommentCount = -1;

   public Guide() {
      this(NEW_GUIDE_ID);
   }

   public Guide(int guideid) {
      mGuideid = guideid;
   }

   public ArrayList<Document> getDocuments() {
      return mDocuments;
   }

   public void setDocuments(ArrayList<Document> documents) {
      mDocuments = documents;
   }

   public ArrayList<Comment> getComments() {
      return mComments;
   }
   public void setComments(ArrayList<Comment> comments) {
      mComments = comments;
   }

   public void setConclusion(String conclusion) {
      mConclusion = conclusion;
   }

   public String getConclusion() {
      return mConclusion;
   }

   public void setGuideid(int guideid) {
      mGuideid = guideid;
   }

   public boolean isNewGuide() {
      return mGuideid == NEW_GUIDE_ID;
   }

   public void setPatrolThreshold(int threshold) {
      mPatrolThreshold = threshold;
   }

   public void addTool(Item tool) {
      mTools.add(tool);
   }

   public int getNumTools() {
      return mTools.size();
   }

   public ArrayList<Item> getTools() {
      return mTools;
   }

   public ArrayList<Item> getParts() {
      return mParts;
   }

   public void setCanEdit(boolean canEdit) {
      mCanEdit = canEdit;
   }

   public void setType(String type) {
      mType = type;
   }

   public String getType() {
      return mType;
   }

   public boolean isTeardown() { return mType.equalsIgnoreCase("teardown"); }

   public void addPart(Item part) {
      mParts.add(part);
   }

   public int getNumParts() {
      return mParts.size();
   }

   public void setStepList(ArrayList<GuideStep> steps) {
      mSteps = steps;
   }

   public void addStep(GuideStep step) {
      mSteps.add(step);
   }

   public void addStep(GuideStep step, int position) {
      mSteps.add(position, step);

      for (int i = 1; i < mSteps.size(); i++) {
         mSteps.get(i).setStepNum(i);
      }
   }

   public void deleteStep(GuideStep step) {
      mSteps.remove(step);
   }

   public void setPublic(boolean isPublic) {
      mPublic = isPublic;
   }

   public boolean isPublic() {
      return mPublic;
   }

   public void setLocale(String locale) {
      mLocale = locale;
   }

   public String getLocale() {
      return mLocale;
   }

   public int getNumSteps() {
      return mSteps.size();
   }

   public ArrayList<GuideStep> getSteps() {
      return mSteps;
   }

   public GuideStep getStep(int position) {
      return mSteps.get(position);
   }

   public GuideStep getStepById(int stepid) {
      for (GuideStep step : mSteps) {
         if (step.getStepid() == stepid) {
            return step;
         }
      }

      return null;
   }

   public boolean hasNewStep() {
      for (GuideStep step : mSteps) {
         if (step.isNewStep()) {
            return true;
         }
      }
      return false;
   }

   public int getGuideid() {
      return mGuideid;
   }

   public void setTitle(String title) {
      mTitle = title;
   }

   public String getTitle() {
      return mTitle;
   }

   public void setTopic(String topic) {
      mTopic = topic;
   }

   public String getTopic() {
      return mTopic;
   }

   public void setAuthor(String author) {
      mAuthor = author;
   }

   public String getAuthor() {
      return mAuthor;
   }

   public void setTimeRequired(String timeRequired) {
      mTimeRequired = timeRequired;
   }

   public void setDifficulty(String difficulty) {
      mDifficulty = difficulty;
   }

   public String getDifficulty() {
      return mDifficulty;
   }

   public void setIntroductionRendered(String introductionRendered) {
      mIntroductionRendered = introductionRendered;
   }

   public void setIntroductionRaw(String introductionRaw) {
      mIntroductionRaw = introductionRaw;
   }

   public String getIntroductionRaw() {
      return mIntroductionRaw;
   }

   public String getIntroductionRendered() {
      return mIntroductionRendered;
   }

   public void setIntroImage(Image image) {
      mIntroImage = image;
   }

   public Image getIntroImage() {
      return mIntroImage;
   }

   public void setSummary(String summary) {
      mSummary = summary;
   }

   public String getSummary() {
      return mSummary;
   }

   public String getSubject() {
      return mSubject;
   }

   public void setSubject(String subject) {
      mSubject = subject;
   }

   public void setRevisionid(Integer revisionid) {
      mRevisionid = revisionid;
   }

   public Integer getRevisionid() {
      return mRevisionid;
   }

   public boolean isFavorited() {
      return mFavorited;
   }

   public void setFavorited(boolean favorited) {
      mFavorited = favorited;
   }

   public void setModifiedDate(double modifiedDate) {
      mModifiedDate = modifiedDate;
   }

   public void setPrereqModifiedDate(double prereqModifiedDate) {
      mPrereqModifiedDate = prereqModifiedDate;
   }

   /**
    * Returns the guide's modified date including prereq modifications.
    */
   public double getAbsoluteModifiedDate() {
      return Math.max(mModifiedDate, mPrereqModifiedDate);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof Guide)) {
         return false;
      }
      Guide lhs = (Guide) o;
      return mGuideid == lhs.mGuideid;
   }

   public String toString() {
      return "{" + mGuideid + "\n" + mRevisionid + "\n" + mTitle + "\n" + mTopic + "\n" +
       mAuthor + "\n" + mTimeRequired + "\n" + mDifficulty + "\n" + mIntroductionRendered + "\n" +
       mSummary + "\n\n" + mSteps + "\n" + mSummary + "}";
   }

   public void setCompleted(boolean completed) {
      mCompleted = completed;
   }

   public boolean getCompleted() {
      return mCompleted;
   }

   /**
    * Used only for overriding a stale comment list.  We need to do this because we:
    * a) don't have a comments api endpoint
    * b) we can't refetch the whole guide without wreaking havok on the layout, and on some guides
    *    that is a lot of data.
    * c) when new comments are added, we want to show the user their comments are counted.
    * d) can't pass ArrayLists' of Comment objects like we used to do between CommentActivity and GuideViewActivity
    *    because android 7+ will crash with a TransactionTooLargeException
    *    So we have to fake the number.
    * @param count
    */
   public void setCommentCount(int count) {
      mFakeCommentCount = count;
   }

   public int getCommentCount() {
      if (mFakeCommentCount > 1) {
         return mFakeCommentCount;
      }

      int count = mComments.size();

      for (Comment comment : mComments) {
         count += comment.mReplies.size();
      }

      return count;
   }

   public void setFeaturedDocument(int documentid) {
      mFeaturedDocumentId = documentid;
   }

   public Document getFeaturedDocument() {
      Document result = null;

      for (int i = 0; i < mDocuments.size(); i++) {
         if (mDocuments.get(i).id == mFeaturedDocumentId) {
            result = mDocuments.get(i);
         }
      }
      return result;
   }
}
