package in.dsij.pas.net.respose;

import java.util.List;

public class ResRiskAssessmentQA {

    private List<listQuestionsEntity> list;

    public List<listQuestionsEntity> getList() {
        return list;
    }

    public void setList(List<listQuestionsEntity> list) {
        this.list = list;
    }

    public static class listQuestionsEntity {

        private long riskQuestionId;
        private String question;
        private List<AnsEntity> ans;

        public long getRiskQuestionId() {
            return riskQuestionId;
        }

        public void setRiskQuestionId(long riskQuestionId) {
            this.riskQuestionId = riskQuestionId;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public List<AnsEntity> getAns() {
            return ans;
        }

        public void setAns(List<AnsEntity> ans) {
            this.ans = ans;
        }

        public static class AnsEntity{
            private long riskAnswerId;
            private String answer;
            private long answerPoints;
            private long riskQuestionId;
            private String question;

            public long getRiskAnswerId() {
                return riskAnswerId;
            }

            public void setRiskAnswerId(long riskAnswerId) {
                this.riskAnswerId = riskAnswerId;
            }

            public String getAnswer() {
                return answer;
            }

            public void setAnswer(String answer) {
                this.answer = answer;
            }

            public long getAnswerPoints() {
                return answerPoints;
            }

            public void setAnswerPoints(long answerPoints) {
                this.answerPoints = answerPoints;
            }

            public long getRiskQuestionId() {
                return riskQuestionId;
            }

            public void setRiskQuestionId(long riskQuestionId) {
                this.riskQuestionId = riskQuestionId;
            }

            public String getQuestion() {
                return question;
            }

            public void setQuestion(String question) {
                this.question = question;
            }
        }
    }

}
