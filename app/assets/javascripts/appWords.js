var app = angular.module('quizApp', []);
/*
app.directive('wordsList', function() {
	return {
		restrict: 'AE',
		scope: {},
		templateUrl: '/views/template',
		link: function(scope) {
			
		}
	}
});
*/
app.directive('quiz', function(quizFactory, $timeout) {
	return {
		restrict: 'AE',
		scope: {},
		templateUrl: '/views/template',
		link: function(scope) {
			scope.start = function() {
				scope.id = 0;
				scope.quizOver = false;
				scope.inProgress = true;
				scope.getQuestion();
			};

			scope.reset = function() {
				scope.inProgress = false;
				scope.quizOver = false;
				scope.score = 0;
			};
			
			scope.getQuestion = function() {
				var q = quizFactory.getQuestion(scope.id);
				if(q) {
					scope.question = q.question;
					scope.options = q.options;
					scope.answer = q.answer;
					scope.answerMode = true;
				} else {
					scope.quizOver = true;
				}
			};
			
			scope.tap = function(option, $event) {
				if (scope.answerMode) {
					$event.stopPropagation();
					scope.answerMode = false;
					if (option.logos == scope.options[scope.answer].logos) {
						option.answeredClass="panel-success";
						scope.score++;
						scope.correctAns = true;
						$timeout(scope.nextQuestion, 1000);
					} else {
						scope.correctAns = false;
						option.answeredClass="panel-danger";
						scope.options[scope.answer].answeredClass = "panel-success";
					}
				} // else case is handled in tapNext
			};
			
			scope.tapNext = function() {
				if (!scope.answerMode) { scope.nextQuestion(); }
			};
			
			scope.checkAnswer = function() {
				if(!$('input[name=answer]:checked').length) return;

				var ans = $('input[name=answer]:checked').val();

				if(ans == scope.options[scope.answer].logos) {
					scope.score++;
					scope.correctAns = true;
				} else {
					scope.correctAns = false;
				}

				scope.answerMode = false;
			};

			scope.nextQuestion = function() {
				scope.correctAns = false;
				scope.answerMode = true;
				scope.options.forEach(function(item){ item.answeredClass=undefined; });
				scope.id++;
				scope.getQuestion();
			};

			scope.reset();
		}
	};
});

app.factory('quizFactory', function() {
	var questions = quizz;
	
	return {
		getQuestion: function(id) {
			if(id < questions.length) {
				return questions[id];
			} else {
				return false;
			}
		}
	};
});