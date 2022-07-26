using System;
using System.Collections;
using System.Collections.Generic;
using System.Security.Principal;
using UnityEngine;
using UnityEngine.Tilemaps;

namespace My2DGameKit {
    [RequireComponent(typeof(My2DGameKit.CharacterController2D))]
    [RequireComponent(typeof(Animator))]
    public class PlayerCharacter : MonoBehaviour {
        protected static My2DGameKit.PlayerCharacter mPlayerInstance;
        public static My2DGameKit.PlayerCharacter PlayerInstance {
            get {
                return My2DGameKit.PlayerCharacter.mPlayerInstance;
            }
        }

        public My2DGameKit.InventoryController InventoryController {
            get {
                return this.mInventoryController;
            }
        }

        public SpriteRenderer spriteRenderer;
        public Light characterLight;
        public LensFlare characterLensFlare;
        public My2DGameKit.Damageable damageable;
        public My2DGameKit.Damager meleeDamager;
        public Transform facingLeftBulletSpawnPoint;
        public Transform facingRightBulletSpawnPoint;
        public Transform facingTopLeftBulletSpawnPoint;
        public Transform facingTopRightBulletSpawnPoint;
        public My2DGameKit.BulletPool bulletPool;
        public Transform cameraFollowTarget;

        public float maxSpeed = 10f;
        public float groundAcceleration = 100f;
        public float groundDeceleration = 100f;
        [Range(0f, 1f)] public float pushingSpeedProportion;

        [Range(0f, 1f)] public float airborneAccelProportion;
        [Range(0f, 1f)] public float airborneDecelProportion;

        [Range(0f, 1f)] public float underWaterAccelProportion;
        [Range(0f, 1f)] public float underWaterDecelProportion;
        [Range(0f, 1f)] public float underWaterSpeedProportion;
        [Range(0f, 1f)] public float underWaterGravityProportion;
        [Range(0f, 1f)] public float underWaterJumpSpeedProportion;
        [Range(1f, 5f)] public float underWaterJumpAbortSpeedReductionProportion;

        [Range(0f, 1f)] public float onLadderAccelProportion;
        [Range(0f, 1f)] public float onLadderDecelProportion;
        [Range(0f, 1f)] public float onLadderSpeedProportion;
        [Range(0f, 1f)] public float onLadderJumpSpeedProportion;

        public float gravity = 50f;
        public float jumpSpeed = 20f;
        public float jumpAbortSpeedReduction = 100f;

        [Range(kMinHurtJumpAngle, kMaxHurtJumpAngle)] public float hurtJumpAngle = 45f;
        public float hurtJumpSpeed = 5f;
        public float flickeringDuration = 0.1f;

        public float meleeAttackDashSpeed = 5f;
        public bool dashWhileAirborne = false;

        public My2DGameKit.RandomAudioPlayer footstepAudioPlayer;
        public My2DGameKit.RandomAudioPlayer landingAudioPlayer;
        public My2DGameKit.RandomAudioPlayer hurtAudioPlayer;
        public My2DGameKit.RandomAudioPlayer meleeAttackAudioPlayer;
        public My2DGameKit.RandomAudioPlayer rangedAttackAudioPlayer;

        public float shotsPerSecond = 1f;
        public float bulletSpeed = 5f;
        public float holdingGunTimeoutDuration = 10f;
        public bool rightBulletSpawnPointAnimated = true;

        public float cameraHorizontalFacingOffset;
        public float cameraHorizontalSpeedOffset;
        public float cameraVerticalInputOffset;
        public float maxHorizontalDeltaDampTime;
        public float maxVerticalDeltaDampTime;
        public float verticalCameraOffsetDelay;

        public bool spriteOriginallyFacesLeft;

        public CharacterController2D mCharacterController2D;
        protected Animator mAnimator;
        protected CapsuleCollider2D mCapsule;
        protected Transform mTransform;
        protected Vector2 mMoveVector;
        protected List<My2DGameKit.Pushable> mCurrentPushables = new List<My2DGameKit.Pushable>(4);
        protected My2DGameKit.Pushable mCurrentPushable;
        protected float mTanHurtJumpAngle;
        protected WaitForSeconds mFlickeringWait;
        protected Coroutine mFlickerCoroutine;
        protected Transform mCurrentBulletSpawnPoint;
        protected float mShotSpawnGap;
        protected WaitForSeconds mShotSpawnWait;
        protected Coroutine mShootingCoroutine;
        protected float mNextShotTime;
        protected bool mIsFiring;
        protected float mShotTimer;
        protected float mHoldingGunTimeRemaining;
        protected TileBase mCurrentSurface;
        protected float mCamFollowHorizontalSpeed;
        protected float mCamFollowVerticalSpeed;
        protected float mVerticalCameraOffsetTimer;
        protected My2DGameKit.InventoryController mInventoryController;

        protected My2DGameKit.Checkpoint mLastCheckpoint = null;
        protected Vector2 mStartingPosition = Vector2.zero;
        protected bool mStartingFacingLeft = false;

        protected bool mInPause = false;

        protected readonly int mHashHorizontalSpeedParam = Animator.StringToHash("HorizontalSpeed");
        protected readonly int mHashVerticalSpeedParam = Animator.StringToHash("VerticalSpeed");
        protected readonly int mHashGroundedParam = Animator.StringToHash("Grounded");
        protected readonly int mHashCrouchingParam = Animator.StringToHash("Crouching");
        protected readonly int mHashPushingParam = Animator.StringToHash("Pushing");
        protected readonly int mHashTimeoutParam = Animator.StringToHash("Timeout");
        protected readonly int mHashRespawnParam = Animator.StringToHash("Respawn");
        protected readonly int mHashDeadParam = Animator.StringToHash("Dead");
        protected readonly int mHashHurtParam = Animator.StringToHash("Hurt");
        protected readonly int mHashForcedRespawnParam = Animator.StringToHash("ForcedRespawn");
        protected readonly int mHashMeleeAttackParam = Animator.StringToHash("MeleeAttack");
        protected readonly int mHashHoldingGunParam = Animator.StringToHash("HoldingGun");
        protected readonly int mHashHandUpParam = Animator.StringToHash("HandUp");

        protected const float kMinHurtJumpAngle = 0.001f;
        protected const float kMaxHurtJumpAngle = 89.999f;
        protected const float kGroundedStickingVelocityMultiplier = 3f;    // This is to help the character stick to vertically moving platforms.

        //used in non alloc version of physic function
        protected ContactPoint2D[] mContactsBuffer = new ContactPoint2D[16];

        //==============================================================================
        // Метод, вызываемый при инициализации текущей сцены
        private void Awake() {
            My2DGameKit.PlayerCharacter.mPlayerInstance = this;

            this.mCharacterController2D = this.GetComponent<My2DGameKit.CharacterController2D>();
            this.mAnimator  = this.GetComponent<Animator>();
            this.mCapsule   = this.GetComponent<CapsuleCollider2D>();
            this.mTransform = this.transform;
            this.mInventoryController = this.GetComponent<My2DGameKit.InventoryController>();

            this.mCurrentBulletSpawnPoint = this.spriteOriginallyFacesLeft ? this.facingLeftBulletSpawnPoint : this.facingRightBulletSpawnPoint;
        }

        //==============================================================================
        // Метод, вызываемый при старте текущей сцены
        private void Start() {
            this.Initialise();
        }

        //==============================================================================
        private void OnEnable() {
            this.Initialise();
        }

        //==============================================================================
        // Метод для считывания вертикальной скорости персонажа
        public float GetVerticalSpeed() {
            float playerVerticalSpeed = 0f;

            if(this.mAnimator != null) {
                playerVerticalSpeed = this.mAnimator.GetFloat(this.mHashVerticalSpeedParam);
            }

            return playerVerticalSpeed;
        }

        //==============================================================================
        public static float GetUnderWaterSpeedProportion() {
            float underWaterSpeedProportionLocal = 1f;

            if(My2DGameKit.PlayerCharacter.mPlayerInstance != null) {
                underWaterSpeedProportionLocal = My2DGameKit.PlayerCharacter.mPlayerInstance.underWaterSpeedProportion;
            }

            return underWaterSpeedProportionLocal;
        }

        //==============================================================================
        // Метод для считывания ускорения свободного падения
        public float GetGravity() {
            float gravityLocal = this.gravity;

            // Если главный персонаж находится под водой
            if(My2DGameKit.CharacterTriggerWater.IsCharacterUnderWater() == true) {
                gravityLocal *= this.underWaterGravityProportion;
            }

            return gravityLocal;
        }

        //==============================================================================
        // Метод для считывания силы прыжка
        public float GetJumpSpeed() {
            float jumpSpeedLocal = this.jumpSpeed;

            // Если главный персонаж находится под водой
            if(My2DGameKit.CharacterTriggerWater.IsCharacterUnderWater() == true) {
                jumpSpeedLocal *= this.underWaterJumpSpeedProportion;
            }
            // Иначе, если главный персонаж находится на летснице
            else if(My2DGameKit.CharacterTriggerLadder.IsCharacterOnLadder() == true) {
                jumpSpeedLocal *= this.onLadderJumpSpeedProportion;
            }

            return jumpSpeedLocal;
        }

        //==============================================================================
        public float GetJumpAbortSpeedReduction() {
            float jumpAbortSpeedReductionLocal = this.jumpAbortSpeedReduction;

            // Если главный персонаж находится под водой
            if(My2DGameKit.CharacterTriggerWater.IsCharacterUnderWater() == true) {
                jumpAbortSpeedReductionLocal *= this.underWaterJumpSpeedProportion;
            }

            return jumpAbortSpeedReductionLocal;
        }

        //==============================================================================
        public void Initialise() {
            this.hurtJumpAngle = Mathf.Clamp(hurtJumpAngle, My2DGameKit.PlayerCharacter.kMinHurtJumpAngle, My2DGameKit.PlayerCharacter.kMaxHurtJumpAngle);
            this.mTanHurtJumpAngle = Mathf.Tan(Mathf.Deg2Rad * hurtJumpAngle);
            this.mFlickeringWait = new WaitForSeconds(this.flickeringDuration);

            if(this.meleeDamager != null) {
                this.meleeDamager.DisableDamage();
            }

            this.mShotSpawnGap = 1f / shotsPerSecond;
            this.mNextShotTime = Time.time;
            this.mShotSpawnWait = new WaitForSeconds(this.mShotSpawnGap);

            if (!Mathf.Approximately(this.maxHorizontalDeltaDampTime, 0f)) {
                float maxHorizontalDelta = this.maxSpeed * this.cameraHorizontalSpeedOffset + this.cameraHorizontalFacingOffset;
                this.mCamFollowHorizontalSpeed = maxHorizontalDelta / this.maxHorizontalDeltaDampTime;
            }

            if (!Mathf.Approximately(this.maxVerticalDeltaDampTime, 0f)) {
                float maxVerticalDelta = this.cameraVerticalInputOffset;
                this.mCamFollowVerticalSpeed = maxVerticalDelta / this.maxVerticalDeltaDampTime;
            }

            My2DGameKit.SceneLinkedSMB<My2DGameKit.PlayerCharacter>.Initialise(this.mAnimator, this);

            this.mStartingPosition = this.transform.position;
            this.mStartingFacingLeft = this.GetFacing() < 0.0f;

            this.UpdateFacingExitFromCar();
        }

        //==============================================================================
        // Метод, вызываемый при столкновении с другими объектами
        private void OnTriggerEnter2D(Collider2D paramOther) {
            if(paramOther != null) {
                My2DGameKit.Pushable pushable = paramOther.GetComponent<My2DGameKit.Pushable>();

                if (pushable != null) {
                    if(this.mCurrentPushables == null) {
                        this.mCurrentPushables = new List<My2DGameKit.Pushable>();
                    }

                    this.mCurrentPushables.Add(pushable);
                }
            }
        }

        //==============================================================================
        // Метод, вызываемый при окончании столкновения с другими объектами
        private void OnTriggerExit2D(Collider2D paramOther) {
            if(paramOther != null) {
                My2DGameKit.Pushable pushable = paramOther.GetComponent<My2DGameKit.Pushable>();

                if (pushable != null && this.mCurrentPushables != null) {
                    if (this.mCurrentPushables.Contains(pushable)) {
                        this.mCurrentPushables.Remove(pushable);
                    }
                }
            }
        }

        //==============================================================================
        // Метод, вызываемый при обновлении текущей сцены
        private void Update() {
            if(My2DGameKit.PlayerInput.Instance != null) {
                if (My2DGameKit.PlayerInput.Instance.Pause.Down) {
                    if (!this.mInPause) {
                        if (My2DGameKit.ScreenFader.IsFading) {
                            return;
                        }

                        My2DGameKit.PlayerInput.Instance.ReleaseControl(false);
                        My2DGameKit.PlayerInput.Instance.Pause.GainControl();

                        this.mInPause = true;
                        Time.timeScale = 0;
                        UnityEngine.SceneManagement.SceneManager.LoadSceneAsync("UIMenus", UnityEngine.SceneManagement.LoadSceneMode.Additive);
                    }
                    else {
                        this.Unpause();
                    }
                }
            }
        }

        //==============================================================================
        // Метод, вызываемый при обновлении текущей сцены
        private void FixedUpdate() {
            if(this.mCharacterController2D != null) {
                this.mCharacterController2D.Move(this.mMoveVector * Time.deltaTime);
            }

            if(this.mAnimator != null) {
                this.mAnimator.SetFloat(this.mHashHorizontalSpeedParam, this.mMoveVector.x);
                this.mAnimator.SetFloat(this.mHashVerticalSpeedParam, this.mMoveVector.y);
            }

            this.UpdateBulletSpawnPointPositions();
            this.UpdateCameraFollowTargetPosition();
        }

        //==============================================================================
        public void Unpause() {
            // if the timescale is already > 0, we 
            if (Time.timeScale > 0) {
                return;
            }

            StartCoroutine(this.UnpauseCoroutine());
        }

        //==============================================================================
        protected IEnumerator UnpauseCoroutine() {
            Time.timeScale = 1;
            UnityEngine.SceneManagement.SceneManager.UnloadSceneAsync("UIMenus");

            if(My2DGameKit.PlayerInput.Instance != null) {
                My2DGameKit.PlayerInput.Instance.GainControl();
            }

            // we have to wait for a fixed update so the pause button state change, otherwise we can get in case were the update
            // of this script happen BEFORE the input is updated, leading to setting the game in pause once again
            yield return new WaitForFixedUpdate();
            yield return new WaitForEndOfFrame();

            this.mInPause = false;
        }

        //==============================================================================
        // Protected functions.
        protected void UpdateBulletSpawnPointPositions() {
            if (this.rightBulletSpawnPointAnimated) {
                Vector2 leftPosition = this.facingRightBulletSpawnPoint.localPosition;
                leftPosition.x *= -1f;
                this.facingLeftBulletSpawnPoint.localPosition = leftPosition;
            }
            else {
                Vector2 rightPosition = this.facingLeftBulletSpawnPoint.localPosition;
                rightPosition.x *= -1f;
                this.facingRightBulletSpawnPoint.localPosition = rightPosition;
            }
        }

        //==============================================================================
        protected void UpdateCameraFollowTargetPosition() {
            float newLocalPosX;
            float newLocalPosY = 0f;

            float desiredLocalPosX = (this.spriteOriginallyFacesLeft ^ this.spriteRenderer.flipX ? -1f : 1f) * this.cameraHorizontalFacingOffset;
            desiredLocalPosX += this.mMoveVector.x * this.cameraHorizontalSpeedOffset;

            if (Mathf.Approximately(this.mCamFollowHorizontalSpeed, 0f)){
                newLocalPosX = desiredLocalPosX;
            }
            else {
                newLocalPosX = Mathf.Lerp(cameraFollowTarget.localPosition.x, desiredLocalPosX, this.mCamFollowHorizontalSpeed * Time.deltaTime);
            }

            bool moveVertically = false;

            if (!Mathf.Approximately(My2DGameKit.PlayerInput.Instance.Vertical.Value, 0f)) {
                this.mVerticalCameraOffsetTimer += Time.deltaTime;

                if (this.mVerticalCameraOffsetTimer >= verticalCameraOffsetDelay) {
                    moveVertically = true;
                }
            }
            else {
                moveVertically = true;
                this.mVerticalCameraOffsetTimer = 0f;
            }

            if (moveVertically) {
                float desiredLocalPosY = My2DGameKit.PlayerInput.Instance.Vertical.Value * this.cameraVerticalInputOffset;

                if (Mathf.Approximately(this.mCamFollowVerticalSpeed, 0f)) {
                    newLocalPosY = desiredLocalPosY;
                }
                else {
                    newLocalPosY = Mathf.MoveTowards(this.cameraFollowTarget.localPosition.y, desiredLocalPosY, this.mCamFollowVerticalSpeed * Time.deltaTime);
                }
            }

            cameraFollowTarget.localPosition = new Vector2(newLocalPosX, newLocalPosY);
        }

        //==============================================================================
        protected IEnumerator Flicker() {
            float timer = 0f;

            while (timer < this.damageable.invulnerabilityDuration) {
                if(this.spriteRenderer != null) {
                    this.spriteRenderer.enabled = !this.spriteRenderer.enabled;
                }

                if(this.characterLight != null) {
                    this.characterLight.enabled = !this.characterLight.enabled;
                }

                if(this.characterLensFlare != null) {
                    this.characterLensFlare.enabled = !this.characterLensFlare.enabled;
                }

                yield return this.mFlickeringWait;

                timer += flickeringDuration;
            }

            if(this.spriteRenderer != null) {
                this.spriteRenderer.enabled = true;
            }

            if(this.characterLight != null) {
                this.characterLight.enabled = true;
            }

            if(this.characterLensFlare != null) {
                this.characterLensFlare.enabled = true;
            }
        }

        //==============================================================================
        protected IEnumerator Shoot() {
            if(My2DGameKit.PlayerInput.Instance != null && PlayerContainer.GetTypePlayer() == PlayerContainer.TypePlayer.Character) {
                while (PlayerInput.Instance.RangedAttack.Held || PlayerContainer.GetStatePlayerAttack() == PlayerContainer.StatePlayerAttack.Yes) {
                    if(AttackPlayerCharacter.Instance != null) {
                        AttackPlayerCharacter.Instance.StartAttack();
                    }

/*
                    if (Time.time >= this.mNextShotTime) {
                        this.SpawnBullet();
                        this.mNextShotTime = Time.time + this.mShotSpawnGap;
                    }
*/

                    yield return null;
                }
            }
        }

        //==============================================================================
        protected void SpawnBullet() {
            try {
                // we check if there is a wall between the player and the bullet spawn position, if there is, we don't spawn a bullet
                // otherwise, the player can "shoot throught wall" because the arm extend to the other side of the wall
                Vector2 testPosition = this.transform.position;
                testPosition.y = this.mCurrentBulletSpawnPoint.position.y;
                
                Vector2 direction = (Vector2)this.mCurrentBulletSpawnPoint.position - testPosition;
                float distance = direction.magnitude;
                direction.Normalize();

                RaycastHit2D[] results = new RaycastHit2D[12];

                if (Physics2D.Raycast(testPosition, direction, this.mCharacterController2D.ContactFilter, results, distance) > 0) {
                    return;
                }

                My2DGameKit.BulletObject bullet = bulletPool.Pop(this.mCurrentBulletSpawnPoint.position);
                bool facingLeft = this.mCurrentBulletSpawnPoint == this.facingLeftBulletSpawnPoint;

                if(bullet != null) {
                    bullet.rigidbody2D.velocity = new Vector2(facingLeft ? -this.bulletSpeed : this.bulletSpeed, 0f);
                    bullet.spriteRenderer.flipX = facingLeft ^ bullet.bullet.spriteOriginallyFacesLeft;
                }

                if(this.rangedAttackAudioPlayer != null) {
                    this.rangedAttackAudioPlayer.PlayRandomSound();
                }
            }
            catch(Exception exception) {
                Debug.Log("Error PlayerCharacter.SpawnBullet(): \n" + exception.Message);
            }
        }

        //==============================================================================
        // Public functions - called mostly by StateMachineBehaviours in the character's Animator Controller but also by Events.
        public void SetMoveVector(Vector2 paramNewMoveVector) {
            this.mMoveVector = paramNewMoveVector;
        }

        //==============================================================================
        public void SetHorizontalMovement(float paramNewHorizontalMovement) {
            this.mMoveVector.x = paramNewHorizontalMovement;
        }

        //==============================================================================
        public void SetVerticalMovement(float paramNewVerticalMovement) {
            this.mMoveVector.y = paramNewVerticalMovement;
        }

        //==============================================================================
        public void IncrementMovement(Vector2 paramAdditionalMovement) {
            this.mMoveVector += paramAdditionalMovement;
        }

        //==============================================================================
        public void IncrementHorizontalMovement(float paramAdditionalHorizontalMovement) {
            this.mMoveVector.x += paramAdditionalHorizontalMovement;
        }

        //==============================================================================
        public void IncrementVerticalMovement(float paramAdditionalVerticalMovement) {
            this.mMoveVector.y += paramAdditionalVerticalMovement;
        }

        //==============================================================================
        public void GroundedVerticalMovement() {
            this.mMoveVector.y -= this.GetGravity() * Time.deltaTime;

            if (this.mMoveVector.y < 0f) {
                this.mMoveVector.y = 0f;
            }
/*
            if (this.mMoveVector.y < -this.GetGravity() * Time.deltaTime * My2DGameKit.PlayerCharacter.kGroundedStickingVelocityMultiplier) {
                this.mMoveVector.y = -this.GetGravity() * Time.deltaTime * My2DGameKit.PlayerCharacter.kGroundedStickingVelocityMultiplier;
            }
*/
        }

        //==============================================================================
        public Vector2 GetMoveVector() {
            return this.mMoveVector;
        }

        //==============================================================================
        public bool IsFalling() {
            return this.mMoveVector.y < 0f && !this.mAnimator.GetBool(this.mHashGroundedParam);
        }

        //==============================================================================
        public void UpdateFacing() {
            if(My2DGameKit.PlayerInput.Instance != null) {
                bool faceLeft  = My2DGameKit.PlayerInput.Instance.Horizontal.Value < 0f;
                bool faceRight = My2DGameKit.PlayerInput.Instance.Horizontal.Value > 0f;
                bool faceTop   = My2DGameKit.PlayerInput.Instance.Vertical.Value > 0f;

                if (faceTop) {
                    if (faceLeft) {
                        this.SetAnimatorFaceLeft(true);
                        this.mCurrentBulletSpawnPoint = this.facingTopLeftBulletSpawnPoint;
                        this.spriteRenderer.flipX = !this.spriteOriginallyFacesLeft;
                    }
                    else if (faceRight) {
                        this.SetAnimatorFaceLeft(false);
                        this.mCurrentBulletSpawnPoint = this.facingTopRightBulletSpawnPoint;
                        this.spriteRenderer.flipX = this.spriteOriginallyFacesLeft;
                    }
                }
                else if (faceLeft) {
                    this.SetAnimatorFaceLeft(true);
                    this.spriteRenderer.flipX = !this.spriteOriginallyFacesLeft;
                    this.mCurrentBulletSpawnPoint = this.facingLeftBulletSpawnPoint;
                }
                else if (faceRight) {
                    this.SetAnimatorFaceLeft(false);
                    this.spriteRenderer.flipX = this.spriteOriginallyFacesLeft;
                    this.mCurrentBulletSpawnPoint = this.facingRightBulletSpawnPoint;
                }
            }
        }

        //==============================================================================
        public void UpdateFacing(bool paramFaceLeft) {
            this.SetAnimatorFaceLeft(paramFaceLeft);

            if (paramFaceLeft) {
                this.spriteRenderer.flipX = !this.spriteOriginallyFacesLeft;
                this.mCurrentBulletSpawnPoint = this.facingLeftBulletSpawnPoint;
            }
            else {
                this.spriteRenderer.flipX = this.spriteOriginallyFacesLeft;
                this.mCurrentBulletSpawnPoint = this.facingRightBulletSpawnPoint;
            }
        }

        //==============================================================================
        public void UpdateFacingExitFromCar() {
            if(My2DGameKit.PlayerInput.Instance != null) {
                bool faceLeft = PlayerContainer.GetCarMoveDirection() == GlobalVariables.TypeMoveDirection.Left;
                bool faceRight = PlayerContainer.GetCarMoveDirection() == GlobalVariables.TypeMoveDirection.Right;

                if (faceLeft) {
                    this.spriteRenderer.flipX = !this.spriteOriginallyFacesLeft;
                    this.mCurrentBulletSpawnPoint = this.facingLeftBulletSpawnPoint;
                }
                else if (faceRight) {
                    this.spriteRenderer.flipX = this.spriteOriginallyFacesLeft;
                    this.mCurrentBulletSpawnPoint = this.facingRightBulletSpawnPoint;
                }
            }
        }

        //==============================================================================
        public float GetFacing() {
            return this.spriteRenderer.flipX != this.spriteOriginallyFacesLeft ? -1f : 1f;
        }

        //==============================================================================
        public void SetAnimatorFaceLeft(bool paramFaceLeft) {
            if(this.mAnimator != null) {
                this.mAnimator.SetBool(GlobalVariablesTags.TAG_ANIMATOR_FACE_LEFT, paramFaceLeft);
            }
        }

        //==============================================================================
        // Метод для обработки гоизонтального перемещения главного персонажа
        public void GroundedHorizontalMovement(bool paramUseInput, float paramSpeedScale = 1f) {
            // Если главный персонаж активен в данный момент (не транспортное средство)
            if(PlayerContainer.GetTypePlayer() == PlayerContainer.TypePlayer.Character) {
                float characterAcceleration = this.groundAcceleration;
                float characterDeceleration = this.groundDeceleration;
                float characterMaxSpeed = this.maxSpeed;

                // Если главный персонаж находится под водой
                if(My2DGameKit.CharacterTriggerWater.IsCharacterUnderWater() == true) {
                    characterAcceleration *= this.underWaterAccelProportion;
                    characterDeceleration *= this.underWaterDecelProportion;
                    characterMaxSpeed *= this.underWaterSpeedProportion;
                }

                float desiredSpeed = paramUseInput ? My2DGameKit.PlayerInput.Instance.Horizontal.Value * characterMaxSpeed * paramSpeedScale : 0f;
                float acceleration = paramUseInput && My2DGameKit.PlayerInput.Instance.Horizontal.ReceivingInput ? characterAcceleration : characterDeceleration;
                this.mMoveVector.x = Mathf.MoveTowards(this.mMoveVector.x, desiredSpeed, acceleration * Time.deltaTime);
            }
        }

        //==============================================================================
        public void CheckForCrouching() {
            if(this.mAnimator != null) {
                this.mAnimator.SetBool(this.mHashCrouchingParam, My2DGameKit.PlayerInput.Instance.Vertical.Value < 0f);
            }
        }

        //==============================================================================
        public void CheckForHandUp() {
            if(this.mAnimator != null) {
                this.mAnimator.SetBool(this.mHashHandUpParam, My2DGameKit.PlayerInput.Instance.Vertical.Value > 0f);
            }
        }

        //==============================================================================
        public bool CheckForGrounded() {
            bool wasGrounded = this.mAnimator.GetBool(this.mHashGroundedParam);
            bool grounded = this.mCharacterController2D.IsGrounded;

            if (grounded) {
                this.FindCurrentSurface();

                if (!wasGrounded && this.mMoveVector.y < -1.0f) {
                    // only play the landing sound if falling "fast" enough (avoid small bump playing the landing sound)
                    if(this.landingAudioPlayer != null) {
                        this.landingAudioPlayer.PlayRandomSound(this.mCurrentSurface);
                    }
                }
            }
            else {
                this.mCurrentSurface = null;
            }

            if(this.mAnimator != null) {
                this.mAnimator.SetBool(this.mHashGroundedParam, grounded);
            }

            return grounded;
        }

        //==============================================================================
        public void FindCurrentSurface() {
            if(this.mCharacterController2D != null) {
                Collider2D groundCollider = this.mCharacterController2D.GroundColliders[0];

                if (groundCollider == null) {
                    groundCollider = this.mCharacterController2D.GroundColliders[1];
                }

                if (groundCollider == null) {
                    return;
                }

                TileBase tileBase = PhysicsHelper.FindTileForOverride(groundCollider, this.transform.position, Vector2.down);

                if (tileBase != null) {
                    this.mCurrentSurface = tileBase;
                }
            }
        }

        //==============================================================================
        public void CheckForPushing() {
            bool pushableOnCorrectSide = false;
            My2DGameKit.Pushable previousPushable = this.mCurrentPushable;

            this.mCurrentPushable = null;

            if (this.mCurrentPushables != null) {
                if (this.mCurrentPushables.Count > 0) {
                    bool movingRight = My2DGameKit.PlayerInput.Instance.Horizontal.Value > float.Epsilon;
                    bool movingLeft = My2DGameKit.PlayerInput.Instance.Horizontal.Value < -float.Epsilon;

                    for (int i = 0; i < this.mCurrentPushables.Count; i++)
                    {
                        float pushablePosX = this.mCurrentPushables[i].pushablePosition.position.x;
                        float playerPosX = this.mTransform.position.x;

                        if (pushablePosX < playerPosX && movingLeft || pushablePosX > playerPosX && movingRight) {
                            pushableOnCorrectSide = true;
                            this.mCurrentPushable = this.mCurrentPushables[i];
                            break;
                        }
                    }

                    if (pushableOnCorrectSide) {
                        Vector2 moveToPosition = movingRight ? this.mCurrentPushable.playerPushingRightPosition.position : this.mCurrentPushable.playerPushingLeftPosition.position;
                        moveToPosition.y = this.mCharacterController2D.Rigidbody2D.position.y;
                        this.mCharacterController2D.Teleport(moveToPosition);
                    }
                }
            }

            if(previousPushable != null && this.mCurrentPushable != previousPushable) {
                // we changed pushable (or don't have one anymore), stop the old one sound
                previousPushable.EndPushing();
            }

            if(this.mAnimator != null) {
                this.mAnimator.SetBool(this.mHashPushingParam, pushableOnCorrectSide);
            }
        }

        //==============================================================================
        public void MovePushable() {
            // we don't push ungrounded pushable, avoid pushing floating pushable or falling pushable.
            if (this.mCurrentPushable && this.mCurrentPushable.Grounded) {
                this.mCurrentPushable.Move(this.mMoveVector * Time.deltaTime);
            }
        }

        //==============================================================================
        public void StartPushing() {
            if (this.mCurrentPushable) {
                this.mCurrentPushable.StartPushing();
            }
        }

        //==============================================================================
        public void StopPushing() {
            if(this.mCurrentPushable) {
                this.mCurrentPushable.EndPushing();
            }
        }

        //==============================================================================
        public void UpdateJump() {
            if (!PlayerInput.Instance.Jump.Held && PlayerContainer.GetStatePlayerJump() == PlayerContainer.StatePlayerJump.No && this.mMoveVector.y > 0.0f) {
                this.mMoveVector.y -= this.GetJumpAbortSpeedReduction() * Time.deltaTime;
            }
        }

        //==============================================================================
        // Функция для горизонтального перемещения персонажа в воздухе
        public void AirborneHorizontalMovement() {
            float desiredSpeed = My2DGameKit.PlayerInput.Instance.Horizontal.Value * this.maxSpeed;

            float acceleration;

            if (My2DGameKit.PlayerInput.Instance.Horizontal.ReceivingInput) {
                acceleration = this.groundAcceleration * this.airborneAccelProportion;
            }
            else {
                acceleration = this.groundDeceleration * this.airborneDecelProportion;
            }

            this.mMoveVector.x = Mathf.MoveTowards(this.mMoveVector.x, desiredSpeed, acceleration * Time.deltaTime);
        }

        //==============================================================================
        // Функция для вертикального перемещения персонажа в воздухе
        public void AirborneVerticalMovement() {
            if (Mathf.Approximately(this.mMoveVector.y, 0f) || this.mCharacterController2D.IsCeilinged && this.mMoveVector.y > 0f) {
                this.mMoveVector.y = 0f;
            }

            this.mMoveVector.y -= this.GetGravity() * Time.deltaTime;
        }

        //==============================================================================
        // Функция для горизонтального перемещения персонажа под водой
        public void UnderWaterHorizontalMovement() {
            float desiredSpeed = My2DGameKit.PlayerInput.Instance.Horizontal.Value * this.maxSpeed * this.underWaterSpeedProportion;

            float acceleration;

            if (My2DGameKit.PlayerInput.Instance.Horizontal.ReceivingInput) {
                acceleration = this.groundAcceleration * this.underWaterAccelProportion;
            }
            else {
                acceleration = this.groundDeceleration * this.underWaterDecelProportion;
            }

            this.mMoveVector.x = Mathf.MoveTowards(this.mMoveVector.x, desiredSpeed, acceleration * Time.deltaTime);
        }

        //==============================================================================
        // Функция для вертикального перемещения персонажа под водой
        public void UnderWaterVerticalMovement() {
//            float desiredSpeed = My2DGameKit.PlayerInput.Instance.Vertical.Value * this.maxSpeed * this.underWaterSpeedProportion;
            float desiredSpeed = this.maxSpeed * this.underWaterSpeedProportion;

            if (PlayerInput.Instance.Jump.Held || PlayerContainer.GetStatePlayerJump() == PlayerContainer.StatePlayerJump.Yes) {
                this.mMoveVector.y = Mathf.MoveTowards(this.mMoveVector.y, desiredSpeed, this.groundAcceleration * this.underWaterAccelProportion * Time.deltaTime);
            }
            else {
                this.mMoveVector.y -= this.GetGravity() * Time.deltaTime;
            }
        }

        //==============================================================================
        // Функция для горизонтального перемещения по лестнице
        public void OnLadderHorizontalMovement() {
            this.mMoveVector.x = 0;
        }

        //==============================================================================
        // Функция для вертикального перемещения по лестнице
        public void OnLadderVerticalMovement() {
            float desiredSpeed = My2DGameKit.PlayerInput.Instance.Vertical.Value * this.maxSpeed * this.onLadderSpeedProportion;

            float acceleration;

            if (My2DGameKit.PlayerInput.Instance.Vertical.ReceivingInput) {
                acceleration = this.groundAcceleration * this.onLadderAccelProportion;
            }
            else {
                acceleration = this.groundDeceleration * this.onLadderDecelProportion;
            }

            this.mMoveVector.y = Mathf.MoveTowards(this.mMoveVector.y, desiredSpeed, acceleration * Time.deltaTime);
        }

        //==============================================================================
        public bool CheckOnLadder() {
            bool isPlayerOnLadder = false;

            if(this.mAnimator != null) {
                isPlayerOnLadder = this.mAnimator.GetBool(GlobalVariablesTags.TAG_ANIMATOR_ON_LADDER);
            }

            return isPlayerOnLadder;
        }

        //==============================================================================
        public void SetAnimatorOnLadder(bool paramOnLadder) {
            if(this.mAnimator != null) {
                this.mAnimator.SetBool(GlobalVariablesTags.TAG_ANIMATOR_ON_LADDER, paramOnLadder);
            }
        }

        //==============================================================================
        public bool CheckForJumpInput() {
            return My2DGameKit.PlayerInput.Instance.Jump.Down || 
                   PlayerContainer.GetStatePlayerJump() == PlayerContainer.StatePlayerJump.Yes;
        }

        //==============================================================================
        public static bool CheckForJumpInputStatic() {
            return My2DGameKit.PlayerInput.Instance.Jump.Down || 
                   PlayerContainer.GetStatePlayerJump() == PlayerContainer.StatePlayerJump.Yes;
        }

        //==============================================================================
        public bool CheckForFallInput() {
            return My2DGameKit.PlayerInput.Instance.Vertical.Value < -float.Epsilon && 
                   (My2DGameKit.PlayerInput.Instance.Jump.Down || PlayerContainer.GetStatePlayerJump() == PlayerContainer.StatePlayerJump.Yes);
        }

        //==============================================================================
        public bool MakePlatformFallthrough() {
            int colliderCount = 0;
            int fallthroughColliderCount = 0;

            if(this.mCharacterController2D != null) {
                if(this.mCharacterController2D.GroundColliders != null) {        
                    for (int i = 0; i < this.mCharacterController2D.GroundColliders.Length; i++) {
                        Collider2D currentCollider = this.mCharacterController2D.GroundColliders[i];

                        if(currentCollider == null) {
                            continue;
                        }

                        colliderCount++;

                        if (PhysicsHelper.ColliderHasPlatformEffector(currentCollider)) {
                            fallthroughColliderCount++;
                        }
                    }

                    if (fallthroughColliderCount == colliderCount) {
                        for (int i = 0; i < this.mCharacterController2D.GroundColliders.Length; i++) {
                            Collider2D currentCollider = this.mCharacterController2D.GroundColliders[i];

                            if (currentCollider == null) {
                                continue;
                            }

                            PlatformEffector2D effector;
                            PhysicsHelper.TryGetPlatformEffector (currentCollider, out effector);
                            
                            My2DGameKit.FallthroughReseter reseter = effector.gameObject.AddComponent<My2DGameKit.FallthroughReseter>();

                            if(reseter != null) {
                                reseter.StartFall(effector);
                            }

                            // set invincible for half a second when falling through a platform, as it will make the player "standup"
                            StartCoroutine(this.FallThroughtInvincibility());
                        }
                    }
                }
            }

            return fallthroughColliderCount == colliderCount;
        }

        //==============================================================================
        private IEnumerator FallThroughtInvincibility() {
            if(this.damageable != null) {
                this.damageable.EnableInvulnerability(true);
            }

            yield return new WaitForSeconds(0.5f);

            if(this.damageable != null) {
                this.damageable.DisableInvulnerability();
            }
        }

        //==============================================================================
        public bool CheckForHoldingGun() {
            bool holdingGun = false;

            if (My2DGameKit.PlayerInput.Instance.RangedAttack.Held || PlayerContainer.GetStatePlayerAttack() == PlayerContainer.StatePlayerAttack.Yes ||
               My2DGameKit.PlayerInput.Instance.Vertical.Value > 0f)
            {
                holdingGun = true;

                if(this.mAnimator != null) {
                    this.mAnimator.SetBool(this.mHashHoldingGunParam, true);
                }

                this.mHoldingGunTimeRemaining = this.holdingGunTimeoutDuration;
            }
            else {
                this.mHoldingGunTimeRemaining -= Time.deltaTime;

                if (this.mHoldingGunTimeRemaining <= 0f) {
                    if(this.mAnimator != null) {
                        this.mAnimator.SetBool(this.mHashHoldingGunParam, false);
                    }
                }
            }

            return holdingGun;
        }

        //==============================================================================
        public void CheckAndFireGun() {
            if(My2DGameKit.PlayerInput.Instance != null && this.mAnimator != null) {
                if ((My2DGameKit.PlayerInput.Instance.RangedAttack.Held || PlayerContainer.GetStatePlayerAttack() == PlayerContainer.StatePlayerAttack.Yes) && 
                     this.mAnimator.GetBool(this.mHashHoldingGunParam))
                {
                    if (this.mShootingCoroutine == null) {
                        this.mShootingCoroutine = StartCoroutine(this.Shoot());
                    }
                }

                if (((My2DGameKit.PlayerInput.Instance.RangedAttack.Up || PlayerContainer.GetStatePlayerAttack() == PlayerContainer.StatePlayerAttack.No) || 
                     !this.mAnimator.GetBool(this.mHashHoldingGunParam)) && this.mShootingCoroutine != null)
                {
                    StopCoroutine(this.mShootingCoroutine);
                    this.mShootingCoroutine = null;
                }
            }
        }

        //==============================================================================
        public void ForceNotHoldingGun() {
            if(this.mAnimator != null) {
                this.mAnimator.SetBool(this.mHashHoldingGunParam, false);
            }
        }

        //==============================================================================
        public void EnableInvulnerability() {
            if(this.damageable != null) {
                this.damageable.EnableInvulnerability();
            }
        }

        //==============================================================================
        public void DisableInvulnerability() {
            if(this.damageable != null) {
                this.damageable.DisableInvulnerability();
            }
        }

        //==============================================================================
        public Vector2 GetHurtDirection() {
            Vector2 damageDirection = this.damageable.GetDamageDirection();

            if (damageDirection.y < 0f) {
                return new Vector2(Mathf.Sign(damageDirection.x), 0f);
            }

            float y = Mathf.Abs(damageDirection.x) * this.mTanHurtJumpAngle;

            return new Vector2(damageDirection.x, y).normalized;
        }

        //==============================================================================
        public void OnHurt(My2DGameKit.Damager paramDamager, My2DGameKit.Damageable paramDamageable) {
            // if the player don't have control, we shouldn't be able to be hurt as this wouldn't be fair
            if (!PlayerInput.Instance.HaveControl) {
                return;
            }

            this.UpdateFacing(damageable.GetDamageDirection().x > 0f);
            damageable.EnableInvulnerability();

            if(this.mAnimator != null) {
                this.mAnimator.SetTrigger(this.mHashHurtParam);
            }

            //we only force respawn if helath > 0, otherwise both forceRespawn & Death trigger are set in the animator, messing with each other.
            if(damageable.CurrentHealth > 0 && paramDamager.forceRespawn) {
                if(this.mAnimator != null) {
                    this.mAnimator.SetTrigger(this.mHashForcedRespawnParam);
                }
            }

            if(this.mAnimator != null) {
                this.mAnimator.SetBool(this.mHashGroundedParam, false);
            }

            if(this.hurtAudioPlayer != null) {
                this.hurtAudioPlayer.PlayRandomSound();
            }

            //if the health is < 0, mean die callback will take care of respawn
            if(paramDamager.forceRespawn && damageable.CurrentHealth > 0) {
                StartCoroutine(this.DieRespawnCoroutine(false, true, false));
            }
        }

        //==============================================================================
        public void OnDie() {
            if(this.mAnimator != null) {
                this.mAnimator.SetTrigger(this.mHashDeadParam);
            }

            StartCoroutine(this.DieRespawnCoroutine(true, false, true));
        }

        //==============================================================================
        public void OnHealthSet(Damageable damageable) {
            if(My2DGameKit.HealthUI.Instance != null) {
                My2DGameKit.HealthUI.Instance.ChangeHitPointUI(damageable);
            }

            // Переинициализируем графическое представление количества жизней главного игрока
            My2DGameKit.LivesPlayer.InitLivesPlayer();
        }

        //==============================================================================
        private IEnumerator DieRespawnCoroutine(bool paramResetHealth, bool paramUseCheckPoint, bool paramIsNeedDestroyCharacter) {
            // Делаем пометку, что осуществляется процесс переинициализации игркоа
            PlayerContainer.SetIsStartPlayerReInit(true);
            
            // Отключаем управление главным персонажем
            if(My2DGameKit.PlayerInput.Instance != null) {
                My2DGameKit.PlayerInput.Instance.ReleaseControl(true);
            }

            // Останавливаем скроллинг фона
            BackgroundLayersCount.StopScrolling();

            // Если необходимо уничтожить шлавного персонажа
            if(paramIsNeedDestroyCharacter == true) {
                PlayerContainer.StartDestroyPlayer();
            }
            // Иначе
            else {
                yield return new WaitForSeconds(1.0f); // wait one second before respawing

                yield return StartCoroutine(
                    My2DGameKit.ScreenFader.FadeSceneOut(
                        paramUseCheckPoint ? My2DGameKit.ScreenFader.FadeType.Black : My2DGameKit.ScreenFader.FadeType.GameOver
                    )
                );

                if(!paramUseCheckPoint) {
                    yield return new WaitForSeconds(2f);
                }

                this.Respawn(paramResetHealth, paramUseCheckPoint);

                // Запускаем скроллинг фона
                BackgroundLayersCount.StartScrolling();

                yield return new WaitForEndOfFrame();
                yield return StartCoroutine(My2DGameKit.ScreenFader.FadeSceneIn());

                if(My2DGameKit.PlayerInput.Instance != null) {
                    My2DGameKit.PlayerInput.Instance.GainControl();
                }
            }
        }

        //==============================================================================
        public void StartFlickering() {
            this.mFlickerCoroutine = StartCoroutine(this.Flicker());
        }

        //==============================================================================
        public void StopFlickering() {
            if(this.mFlickerCoroutine != null) {
                StopCoroutine(this.mFlickerCoroutine);
            }

            if(this.spriteRenderer != null) {
                this.spriteRenderer.enabled = true;
            }

            if(this.characterLight != null) {
                this.characterLight.enabled = true;
            }

            if(this.characterLensFlare != null) {
                this.characterLensFlare.enabled = true;
            }
        }

        //==============================================================================
        public bool CheckForMeleeAttackInput() {
            return PlayerInput.Instance.MeleeAttack.Down;
        }

        //==============================================================================
        public void MeleeAttack() {
            if(this.mAnimator != null) {
                this.mAnimator.SetTrigger(this.mHashMeleeAttackParam);
            }
        }

        //==============================================================================
        public void EnableMeleeAttack() {
            if(this.meleeDamager != null) {
                this.meleeDamager.EnableDamage();
                this.meleeDamager.disableDamageAfterHit = true;
            }

            if(this.meleeAttackAudioPlayer != null) {
                this.meleeAttackAudioPlayer.PlayRandomSound();
            }
        }

        //==============================================================================
        public void DisableMeleeAttack() {
            if(this.meleeDamager != null) {
                this.meleeDamager.DisableDamage();
            }
        }

        //==============================================================================
        public void TeleportToColliderBottom() {
            if(this.mCharacterController2D != null && this.mCapsule != null) {
                Vector2 colliderBottom = this.mCharacterController2D.Rigidbody2D.position + this.mCapsule.offset + Vector2.down * this.mCapsule.size.y * 0.5f;
                this.mCharacterController2D.Teleport(colliderBottom);
            }
        }

        //==============================================================================
        public void PlayFootstep() {
            if(this.footstepAudioPlayer != null) {
                this.footstepAudioPlayer.PlayRandomSound(this.mCurrentSurface);
            }

            var footstepPosition = this.transform.position;
            footstepPosition.z -= 1;
            VFXController.Instance.Trigger("DustPuff", footstepPosition, 0, false, null, this.mCurrentSurface);
        }

        //==============================================================================
        public void Respawn(bool paramResetHealth, bool paramUseCheckpoint) {
            // При необходимости восстанавливаем здоровье главного персонажа
            if (paramResetHealth && this.damageable != null) {
                this.damageable.SetHealth(this.damageable.startingHealth);
            }

            // we reset the hurt trigger, as we don't want the player to go back to hurt animation once respawned
            if(this.mAnimator != null) {
                this.mAnimator.ResetTrigger(this.mHashHurtParam);
            }

            if (this.mFlickerCoroutine != null) {
                // we stop flcikering for the same reason
                this.StopFlickering();
            }

            if(this.mAnimator != null) {
                this.mAnimator.SetTrigger(this.mHashRespawnParam);
            }

            if (paramUseCheckpoint && this.mLastCheckpoint != null) {
                this.UpdateFacing(this.mLastCheckpoint.respawnFacingLeft);
                My2DGameKit.GameObjectTeleporter.Teleport(this.gameObject, this.mLastCheckpoint.transform.position);
            }
            else {
                this.UpdateFacing(this.mStartingFacingLeft);
                My2DGameKit.GameObjectTeleporter.Teleport(this.gameObject, this.mStartingPosition);
            }
        }

        //==============================================================================
        public void SetChekpoint(Checkpoint paramCheckpoint) {
            this.mLastCheckpoint = paramCheckpoint;
        }

        //==============================================================================
        // Метод для считывания информации о последней точке восстановления
        public static My2DGameKit.Checkpoint GetLastCheckpoint() {
            My2DGameKit.Checkpoint lastCheckpoint = null;

            if(My2DGameKit.PlayerCharacter.mPlayerInstance != null) {
                lastCheckpoint = My2DGameKit.PlayerCharacter.mPlayerInstance.mLastCheckpoint;
            }

            return lastCheckpoint;
        }

        //==============================================================================
        // This is called by the inventory controller on key grab, so it can update the Key UI.
        public void KeyInventoryEvent() {
            if (My2DGameKit.KeyUI.Instance != null) {
                My2DGameKit.KeyUI.Instance.ChangeKeyUI(this.mInventoryController);
            }
        }
    }
}
